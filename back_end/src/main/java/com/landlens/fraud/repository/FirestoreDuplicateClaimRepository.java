package com.landlens.fraud.repository;

import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.fraud.model.DuplicateClaim;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreDuplicateClaimRepository extends AbstractFirestoreRepository<DuplicateClaim> implements DuplicateClaimRepository {

    public FirestoreDuplicateClaimRepository() {
        super("duplicate_claims", DuplicateClaim.class);
    }

    @Override
    public List<DuplicateClaim> findByPropertyAIdOrPropertyBId(UUID propertyAId, UUID propertyBId) {
        if (propertyAId == null || propertyBId == null) return List.of();
        List<DuplicateClaim> all = findAll();
        return all.stream()
                .filter(c -> (c.getPropertyA() != null && c.getPropertyA().getId() != null && c.getPropertyA().getId().equals(propertyAId))
                        || (c.getPropertyB() != null && c.getPropertyB().getId() != null && c.getPropertyB().getId().equals(propertyBId)))
                .collect(Collectors.toList());
    }
}
