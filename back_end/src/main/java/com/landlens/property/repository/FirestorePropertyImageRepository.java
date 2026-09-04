package com.landlens.property.repository;

import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.property.model.PropertyImage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestorePropertyImageRepository extends AbstractFirestoreRepository<PropertyImage> implements PropertyImageRepository {

    public FirestorePropertyImageRepository() {
        super("property_images", PropertyImage.class);
    }

    @Override
    public List<PropertyImage> findByPropertyIdAndIsActiveTrueOrderByDisplayOrderAsc(UUID propertyId) {
        if (propertyId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("property.id", propertyId.toString())
                    .whereEqualTo("isActive", true)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .filter(java.util.Objects::nonNull)
                    .sorted((img1, img2) -> {
                        Integer o1 = img1.getDisplayOrder() != null ? img1.getDisplayOrder() : Integer.MAX_VALUE;
                        Integer o2 = img2.getDisplayOrder() != null ? img2.getDisplayOrder() : Integer.MAX_VALUE;
                        return o1.compareTo(o2);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching property images: " + propertyId, e);
        }
    }
}
