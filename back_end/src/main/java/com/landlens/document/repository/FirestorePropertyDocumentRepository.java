package com.landlens.document.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.document.model.PropertyDocument;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestorePropertyDocumentRepository extends AbstractFirestoreRepository<PropertyDocument> implements PropertyDocumentRepository {

    public FirestorePropertyDocumentRepository() {
        super("property_documents", PropertyDocument.class);
    }

    @Override
    public List<PropertyDocument> findByPropertyIdAndIsActiveTrue(UUID propertyId) {
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
            throw new RuntimeException("Error searching property documents: " + propertyId, e);
        }
    }
}
