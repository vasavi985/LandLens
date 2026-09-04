package com.landlens.verification.repository;

import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.verification.model.VerificationTimeline;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreVerificationTimelineRepository extends AbstractFirestoreRepository<VerificationTimeline> implements VerificationTimelineRepository {

    public FirestoreVerificationTimelineRepository() {
        super("verification_timelines", VerificationTimeline.class);
    }

    @Override
    public List<VerificationTimeline> findByPropertyIdAndIsActiveTrueOrderByTimestampAsc(UUID propertyId) {
        if (propertyId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("property.id", propertyId.toString())
                    .whereEqualTo("isActive", true)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .filter(java.util.Objects::nonNull)
                    .sorted((t1, t2) -> {
                        if (t1.getTimestamp() == null && t2.getTimestamp() == null) return 0;
                        if (t1.getTimestamp() == null) return 1;
                        if (t2.getTimestamp() == null) return -1;
                        return t1.getTimestamp().compareTo(t2.getTimestamp());
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error listing verification timeline for property: " + propertyId, e);
        }
    }
}
