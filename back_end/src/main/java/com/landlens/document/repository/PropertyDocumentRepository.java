package com.landlens.document.repository;

import com.landlens.document.model.PropertyDocument;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyDocumentRepository {
    Optional<PropertyDocument> findById(UUID id);
    List<PropertyDocument> findAll();
    PropertyDocument save(PropertyDocument propertyDocument);
    void delete(PropertyDocument propertyDocument);
    void deleteById(UUID id);
    long count();

    List<PropertyDocument> findByPropertyIdAndIsActiveTrue(UUID propertyId);
}
