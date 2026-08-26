package com.landlens.property.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.property.model.PropertyVideo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestorePropertyVideoRepository extends AbstractFirestoreRepository<PropertyVideo> implements PropertyVideoRepository {

    public FirestorePropertyVideoRepository() {
        super("property_videos", PropertyVideo.class);
    }

    @Override
    public List<PropertyVideo> findByPropertyIdAndIsActiveTrue(UUID propertyId) {
        if (propertyId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("property.id", propertyId.toString())
                    .whereEqualTo("isActive", true)
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching property videos: " + propertyId, e);
        }
    }
}
