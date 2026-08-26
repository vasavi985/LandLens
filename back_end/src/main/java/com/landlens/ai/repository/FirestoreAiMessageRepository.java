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
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching AI messages by conversation: " + conversationId, e);
        }
    }
}
