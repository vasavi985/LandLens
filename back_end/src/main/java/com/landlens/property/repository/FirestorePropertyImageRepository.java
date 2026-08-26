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
                    .orderBy("displayOrder", Query.Direction.ASCENDING)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching property images: " + propertyId, e);
        }
    }
}
