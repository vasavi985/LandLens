package com.landlens.ai.repository;

import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.ai.model.AiMessage;
import com.landlens.common.AbstractFirestoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreAiMessageRepository extends AbstractFirestoreRepository<AiMessage> implements AiMessageRepository {

    public FirestoreAiMessageRepository() {
        super("ai_messages", AiMessage.class);
    }

    @Override
    public List<AiMessage> findByConversationIdAndIsActiveTrueOrderByTimestampAsc(UUID conversationId) {
        if (conversationId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("conversation.id", conversationId.toString())
                    .whereEqualTo("isActive", true)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .filter(java.util.Objects::nonNull)
                    .sorted((m1, m2) -> {
                        if (m1.getTimestamp() == null && m2.getTimestamp() == null) return 0;
                        if (m1.getTimestamp() == null) return 1;
                        if (m2.getTimestamp() == null) return -1;
                        return m1.getTimestamp().compareTo(m2.getTimestamp());
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching AI messages by conversation: " + conversationId, e);
        }
    }
}
