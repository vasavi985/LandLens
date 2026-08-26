package com.landlens.property.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.property.model.PropertyVisit;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestorePropertyVisitRepository extends AbstractFirestoreRepository<PropertyVisit> implements PropertyVisitRepository {

    public FirestorePropertyVisitRepository() {
        super("property_visits", PropertyVisit.class);
    }

    @Override
    public List<PropertyVisit> findByBuyerIdAndIsActiveTrue(UUID buyerId) {
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
            throw new RuntimeException("Error searching visits by buyer: " + buyerId, e);
        }
    }

    @Override
    public List<PropertyVisit> findByPropertyProviderIdAndIsActiveTrue(UUID providerId) {
        if (providerId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("property.provider.id", providerId.toString())
                    .whereEqualTo("isActive", true)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching visits by provider: " + providerId, e);
        }
    }
}
