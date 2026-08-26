package com.landlens.fraud.repository;

import com.landlens.fraud.model.DuplicateClaim;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DuplicateClaimRepository {
    Optional<DuplicateClaim> findById(UUID id);
    List<DuplicateClaim> findAll();
    DuplicateClaim save(DuplicateClaim duplicateClaim);
    void delete(DuplicateClaim duplicateClaim);
    void deleteById(UUID id);
    long count();

    List<DuplicateClaim> findByPropertyAIdOrPropertyBId(UUID propertyAId, UUID propertyBId);
}
