package com.landlens.verification.repository;

import com.landlens.verification.model.VerificationTimeline;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationTimelineRepository {
    Optional<VerificationTimeline> findById(UUID id);
    List<VerificationTimeline> findAll();
    VerificationTimeline save(VerificationTimeline verificationTimeline);
    void delete(VerificationTimeline verificationTimeline);
    void deleteById(UUID id);
    long count();

    List<VerificationTimeline> findByPropertyIdAndIsActiveTrueOrderByTimestampAsc(UUID propertyId);
}
