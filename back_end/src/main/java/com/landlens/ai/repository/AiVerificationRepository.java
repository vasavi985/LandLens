package com.landlens.ai.repository;

import com.landlens.ai.model.AiVerification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiVerificationRepository {
    Optional<AiVerification> findById(UUID id);
    List<AiVerification> findAll();
    AiVerification save(AiVerification aiVerification);
    void delete(AiVerification aiVerification);
    void deleteById(UUID id);
    long count();

    Optional<AiVerification> findByPropertyIdAndIsActiveTrue(UUID propertyId);
}
