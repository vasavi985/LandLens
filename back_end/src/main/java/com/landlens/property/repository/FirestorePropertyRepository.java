package com.landlens.property.repository;

import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.property.model.Property;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestorePropertyRepository extends AbstractFirestoreRepository<Property> implements PropertyRepository {

    public FirestorePropertyRepository() {
        super("properties", Property.class);
    }

    @Override
    public Optional<Property> findByPropertyCode(String propertyCode) {
        if (propertyCode == null) return Optional.empty();
        try {
            QuerySnapshot snapshot = getCollection().whereEqualTo("propertyCode", propertyCode).limit(1).get().get();
            if (!snapshot.isEmpty()) {
                return Optional.ofNullable(mapToObject(snapshot.getDocuments().get(0).getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error searching property by code: " + propertyCode, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByPropertyCode(String propertyCode) {
        return findByPropertyCode(propertyCode).isPresent();
    }

    @Override
    public List<Property> findByProviderIdAndIsActiveTrue(UUID providerId) {
        if (providerId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("provider.id", providerId.toString())
                    .whereEqualTo("isActive", true)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching property by provider: " + providerId, e);
        }
    }

    @Override
    public List<Property> findByIsActiveTrueOrderByCreatedAtAsc() {
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("isActive", true)
                    .orderBy("createdAt", Query.Direction.ASCENDING)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error listing active properties ordered by createdAt", e);
        }
    }

    @Override
    public List<Property> searchProperties(
            String district,
            String village,
            String state,
            String status,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice) {
        // Fetch all active properties ordered by createdAt
        List<Property> allActive = findByIsActiveTrueOrderByCreatedAtAsc();
        
        // Filter in-memory using Java streams for identical query behavior without index requirement
        return allActive.stream()
                .filter(p -> district == null || district.trim().isEmpty() || (p.getDistrict() != null && p.getDistrict().toLowerCase().contains(district.toLowerCase().trim())))
                .filter(p -> village == null || village.trim().isEmpty() || (p.getVillage() != null && p.getVillage().toLowerCase().contains(village.toLowerCase().trim())))
                .filter(p -> state == null || state.trim().isEmpty() || (p.getState() != null && p.getState().toLowerCase().contains(state.toLowerCase().trim())))
                .filter(p -> status == null || status.trim().isEmpty() || (p.getStatus() != null && p.getStatus().equalsIgnoreCase(status.trim())))
                .filter(p -> category == null || category.trim().isEmpty() || (p.getCategory() != null && p.getCategory().equalsIgnoreCase(category.trim())))
                .filter(p -> minPrice == null || p.getPrice() == null || p.getPrice().compareTo(minPrice) >= 0)
                .filter(p -> maxPrice == null || p.getPrice() == null || p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());
    }
}
