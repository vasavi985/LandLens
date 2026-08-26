package com.landlens.ai.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.ai.model.AiVerification;
import com.landlens.common.AbstractFirestoreRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class FirestoreAiVerificationRepository extends AbstractFirestoreRepository<AiVerification> implements AiVerificationRepository {

    public FirestoreAiVerificationRepository() {
        super("ai_verifications", AiVerification.class);
    }

    @Override
    public Optional<AiVerification> findByPropertyIdAndIsActiveTrue(UUID propertyId) {
        if (propertyId == null) return Optional.empty();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("property.id", propertyId.toString())
                    .whereEqualTo("isActive", true)
                    .limit(1).get().get();
            if (!snapshot.isEmpty()) {
                return Optional.ofNullable(mapToObject(snapshot.getDocuments().get(0).getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error searching AI verification by property: " + propertyId, e);
        }
        return Optional.empty();
    }
}
