package com.landlens.ai.service;

import com.landlens.ai.exception.AiVerificationApiException;
import com.landlens.ai.model.AiVerification;
import com.landlens.ai.repository.AiVerificationRepository;
import com.landlens.property.model.Property;
import com.landlens.property.repository.PropertyRepository;
import com.landlens.user.model.User;
import com.landlens.user.repository.UserRepository;
import com.landlens.verification.model.VerificationTimeline;
import com.landlens.verification.repository.VerificationTimelineRepository;
import com.landlens.notification.service.NotificationService;
import com.landlens.document.repository.PropertyDocumentRepository;
import com.landlens.document.model.PropertyDocument;
import com.landlens.fraud.repository.DuplicateClaimRepository;
import com.landlens.fraud.model.DuplicateClaim;
import com.landlens.fraud.repository.FraudReportRepository;
import com.landlens.fraud.model.FraudReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AiVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(AiVerificationService.class);
    private static final String REASONING_CONTENT_KEY = "reasoning_content";

    @Autowired
    private AiVerificationRepository aiVerificationRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private VerificationTimelineRepository timelineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PropertyDocumentRepository documentRepository;

    @Autowired
    private DuplicateClaimRepository duplicateClaimRepository;

    @Autowired
    private FraudReportRepository fraudReportRepository;

    private final ObjectMapper objectMapper;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Autowired
    public AiVerificationService(AiVerificationRepository aiVerificationRepository,
                                 PropertyRepository propertyRepository,
                                 ObjectMapper objectMapper) {
        this.aiVerificationRepository = aiVerificationRepository;
        this.propertyRepository = propertyRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AiVerification triggerAiVerification(UUID propertyId, UUID userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PropertyDocument> docs = documentRepository.findByPropertyIdAndIsActiveTrue(propertyId);
        List<DuplicateClaim> claims = duplicateClaimRepository.findByPropertyAIdOrPropertyBId(propertyId, propertyId);
        List<FraudReport> frauds = fraudReportRepository.findByPropertyId(propertyId);

        AiVerificationResult result = performLlmVerification(property, docs, frauds, claims);

        AiVerification report = saveVerificationReport(propertyId, property, result);
        boolean statusChanged = updatePropertyStatus(property);
        logVerificationTimeline(property, user, result.trust, statusChanged);
        sendVerificationNotifications(property, result.trust);

        return report;
    }

    private AiVerificationResult performLlmVerification(Property property, List<PropertyDocument> docs, List<FraudReport> frauds, List<DuplicateClaim> claims) {
        AiVerificationResult result = new AiVerificationResult();
        try {
            String context = buildContext(property, docs, frauds, claims);
            String requestBody = buildRequestBody(context);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://integrate.api.nvidia.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                parseLlmResponse(response.body(), result);
            } else {
                throw new AiVerificationApiException("API Call failed with status: " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            applyFallbackVerification(docs, claims, frauds, result, e.getMessage());
        } catch (Exception e) {
            applyFallbackVerification(docs, claims, frauds, result, e.getMessage());
        }
        return result;
    }

    private String buildContext(Property property, List<PropertyDocument> docs, List<FraudReport> frauds, List<DuplicateClaim> claims) {
        StringBuilder context = new StringBuilder();
        context.append("Property Title: ").append(property.getTitle()).append("\n");
        context.append("Property Desc: ").append(property.getDescription()).append("\n");
        context.append("Survey No: ").append(property.getSurveyNumber()).append("\n");
        context.append("Has 360 Image: ").append(property.getThreeSixtyImageUrl() != null).append("\n");
        
        context.append("Documents:\n");
        if (docs == null || docs.isEmpty()) {
            context.append("NONE\n");
        } else {
            for (PropertyDocument doc : docs) {
                context.append("- ").append(doc.getDocumentType()).append(" (OCR: ").append(doc.getOcrStatus()).append(", VERIFY: ").append(doc.getVerificationStatus()).append(")\n");
            }
        }
        
        context.append("Fraud Reports: ").append(frauds != null ? frauds.size() : 0).append("\n");
        context.append("Duplicate Claims: ").append(claims != null ? claims.size() : 0).append("\n");
        return context.toString();
    }

    private String buildRequestBody(String context) throws JsonProcessingException {
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("model", "openai/gpt-oss-120b");
        rootNode.put("temperature", 1);
        rootNode.put("top_p", 1);
        rootNode.put("max_tokens", 1024);
        rootNode.put("stream", false);
        
        ArrayNode messagesArray = rootNode.putArray("messages");
        ObjectNode messageNode = objectMapper.createObjectNode();
        messageNode.put("role", "user");
        messageNode.put("content", "You are an expert real estate auditor. Analyze the following property data and return your evaluation strictly as a JSON object with NO OTHER TEXT or markdown. Keys must be: 'aiTrustScore' (0-100 float), 'forgeryScore' (0-100 float), 'duplicateScore' (0-100 float), 'riskScore' (0-100 float), 'ownershipMatch' (boolean), 'confidence' (0-100 float), 'summary' (string). Data:\n" + context);
        messagesArray.add(messageNode);

        return objectMapper.writeValueAsString(rootNode);
    }

    private void parseLlmResponse(String responseBody, AiVerificationResult result) throws JsonProcessingException {
        JsonNode responseNode = objectMapper.readTree(responseBody);
        String content = responseNode.path("choices").get(0).path("message").path("content").asText();
        
        if (content.startsWith("```json")) {
            content = content.substring(7);
            if (content.endsWith("```")) content = content.substring(0, content.length() - 3);
        } else if (content.startsWith("```")) {
            content = content.substring(3);
            if (content.endsWith("```")) content = content.substring(0, content.length() - 3);
        }
        
        JsonNode llmResult = objectMapper.readTree(content.trim());
        result.trust = llmResult.path("aiTrustScore").asDouble(50.0);
        result.forgery = llmResult.path("forgeryScore").asDouble(50.0);
        result.duplicate = llmResult.path("duplicateScore").asDouble(0.0);
        result.risk = llmResult.path("riskScore").asDouble(0.0);
        result.ownershipMatch = llmResult.path("ownershipMatch").asBoolean(false);
        result.summary = llmResult.path("summary").asText("LLM verification complete.");
        
        JsonNode messageObj = responseNode.path("choices").get(0).path("message");
        if (messageObj.has(REASONING_CONTENT_KEY) && !messageObj.path(REASONING_CONTENT_KEY).isNull()) {
            result.reasoning = messageObj.path(REASONING_CONTENT_KEY).asText();
        }
    }

    private void applyFallbackVerification(List<PropertyDocument> docs, List<DuplicateClaim> claims, List<FraudReport> frauds, AiVerificationResult result, String errorMessage) {
        result.forgery = (docs == null || docs.isEmpty()) ? 50.0 : 5.0;
        result.duplicate = (claims == null || claims.isEmpty()) ? 0.0 : 15.0;
        result.risk = (frauds == null || frauds.isEmpty()) ? 0.0 : 25.0;
        result.trust = Math.max(100.0 - (result.forgery * 0.4 + result.duplicate * 0.4 + result.risk * 0.2), 0.0);
        result.ownershipMatch = (docs != null && !docs.isEmpty());
        result.summary = "Fallback verification used due to AI service timeout. " + errorMessage;
        result.reasoning = "N/A";
    }

    private AiVerification saveVerificationReport(UUID propertyId, Property property, AiVerificationResult result) {
        AiVerification report = aiVerificationRepository.findByPropertyIdAndIsActiveTrue(propertyId)
                .orElseGet(() -> {
                    AiVerification newReport = new AiVerification();
                    newReport.setProperty(property);
                    return newReport;
                });

        report.setAiTrustScore(BigDecimal.valueOf(result.trust));
        report.setForgeryScore(BigDecimal.valueOf(result.forgery));
        report.setDuplicateScore(BigDecimal.valueOf(result.duplicate));
        report.setOwnershipMatch(result.ownershipMatch);
        report.setRiskScore(BigDecimal.valueOf(result.risk));
        report.setConfidence(BigDecimal.valueOf(Math.min(result.trust + 5.0, 100.0)));
        report.setSummary(result.summary);
        report.setReasoning(result.reasoning);
        report.setIsActive(true);
        report.setGeneratedDate(Instant.now());

        return aiVerificationRepository.save(report);
    }

    private boolean updatePropertyStatus(Property property) {
        if ("PENDING_AI".equals(property.getStatus())) {
            property.setStatus("PENDING_GOVT");
            propertyRepository.save(property);
            return true;
        }
        return false;
    }

    private void logVerificationTimeline(Property property, User user, double trust, boolean statusChanged) {
        VerificationTimeline timeline = new VerificationTimeline();
        timeline.setProperty(property);
        timeline.setAction(statusChanged ? "AI_COMPLETED" : "AI_RE_VERIFIED");
        timeline.setRemarks(String.format("AI Trust evaluation finished. Trust Score: %.2f%%.%s", trust, statusChanged ? " Status updated to PENDING_GOVT." : " Status remained unchanged."));
        timeline.setUser(user);
        timeline.setTimestamp(Instant.now());
        timeline.setIsActive(true);
        timelineRepository.save(timeline);
    }

    private void sendVerificationNotifications(Property property, double trust) {
        try {
            if (property.getProvider() != null) {
                notificationService.sendNotification(
                    property.getProvider().getId(),
                    "AI Trust Audit Completed",
                    String.format("AI verification analysis is complete for your property \"%s\". Trust Score: %.2f%%. The listing has been routed to the government verification queue.", property.getTitle(), trust),
                    "AI_AUDIT"
                );
            }

            List<User> officers = userRepository.findByRoleName("GOVERNMENT_OFFICER");
            for (User officer : officers) {
                notificationService.sendNotification(
                    officer.getId(),
                    "New Property Pending Review",
                    String.format("Property \"%s\" has passed AI Trust Audit with a score of %.2f%%. It is now pending your manual records audit.", property.getTitle(), trust),
                    "PENDING_AUDIT"
                );
            }
        } catch (Exception e) {
            logger.error("Failed to send verification notifications", e);
        }
    }

    public AiVerification getReportByPropertyId(UUID propertyId) {
        return aiVerificationRepository.findByPropertyIdAndIsActiveTrue(propertyId)
                .orElseThrow(() -> new RuntimeException("AI report not found for this property"));
    }

    private static class AiVerificationResult {
        double trust = 0.0;
        double forgery = 0.0;
        double duplicate = 0.0;
        double risk = 0.0;
        boolean ownershipMatch = false;
        String summary = "";
        String reasoning = "";
    }
}
