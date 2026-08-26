package com.landlens.property.repository;

import com.landlens.property.model.PropertyVideo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyVideoRepository {
    Optional<PropertyVideo> findById(UUID id);
    List<PropertyVideo> findAll();
    PropertyVideo save(PropertyVideo propertyVideo);
    void delete(PropertyVideo propertyVideo);
    void deleteById(UUID id);
    long count();

    List<PropertyVideo> findByPropertyIdAndIsActiveTrue(UUID propertyId);
}
