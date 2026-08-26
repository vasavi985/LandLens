package com.landlens.verification.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.verification.model.GovernmentVerification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class FirestoreGovernmentVerificationRepository extends AbstractFirestoreRepository<GovernmentVerification> implements GovernmentVerificationRepository {

    public FirestoreGovernmentVerificationRepository() {
        super("government_verifications", GovernmentVerification.class);
    }

    @Override
    public Optional<GovernmentVerification> findByPropertyIdAndIsActiveTrue(UUID propertyId) {
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
            throw new RuntimeException("Error searching government verification for property: " + propertyId, e);
        }
        return Optional.empty();
    }
}
