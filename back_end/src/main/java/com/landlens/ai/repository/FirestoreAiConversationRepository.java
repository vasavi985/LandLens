package com.landlens.ai.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.ai.model.AiConversation;
import com.landlens.common.AbstractFirestoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreAiConversationRepository extends AbstractFirestoreRepository<AiConversation> implements AiConversationRepository {

    public FirestoreAiConversationRepository() {
        super("ai_conversations", AiConversation.class);
    }

    @Override
    public List<AiConversation> findByUserIdAndIsActiveTrue(UUID userId) {
        if (userId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("user.id", userId.toString())
                    .whereEqualTo("isActive", true)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching AI conversations by user: " + userId, e);
        }
    }
}
