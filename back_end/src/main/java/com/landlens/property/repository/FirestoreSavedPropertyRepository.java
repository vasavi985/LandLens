package com.landlens.property.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.property.model.SavedProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreSavedPropertyRepository extends AbstractFirestoreRepository<SavedProperty> implements SavedPropertyRepository {

    public FirestoreSavedPropertyRepository() {
        super("saved_properties", SavedProperty.class);
    }

    @Override
    public List<SavedProperty> findByBuyerIdAndIsActiveTrue(UUID buyerId) {
        if (buyerId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("buyer.id", buyerId.toString())
                    .whereEqualTo("isActive", true)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching saved properties by buyer: " + buyerId, e);
        }
    }

    @Override
    public Optional<SavedProperty> findByBuyerIdAndPropertyIdAndIsActiveTrue(UUID buyerId, UUID propertyId) {
        if (buyerId == null || propertyId == null) return Optional.empty();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("buyer.id", buyerId.toString())
                    .whereEqualTo("property.id", propertyId.toString())
                    .whereEqualTo("isActive", true)
                    .limit(1).get().get();
            if (!snapshot.isEmpty()) {
                return Optional.ofNullable(mapToObject(snapshot.getDocuments().get(0).getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error searching saved property by buyer and property", e);
        }
        return Optional.empty();
    }
}
