package com.landlens.verification.repository;

import com.landlens.verification.model.GovernmentVerification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GovernmentVerificationRepository {
    Optional<GovernmentVerification> findById(UUID id);
    List<GovernmentVerification> findAll();
    GovernmentVerification save(GovernmentVerification governmentVerification);
    void delete(GovernmentVerification governmentVerification);
    void deleteById(UUID id);
    long count();

    Optional<GovernmentVerification> findByPropertyIdAndIsActiveTrue(UUID propertyId);
}
