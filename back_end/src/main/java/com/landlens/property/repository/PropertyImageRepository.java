package com.landlens.property.repository;

import com.landlens.property.model.PropertyImage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyImageRepository {
    Optional<PropertyImage> findById(UUID id);
    List<PropertyImage> findAll();
    PropertyImage save(PropertyImage propertyImage);
    void delete(PropertyImage propertyImage);
    void deleteById(UUID id);
    long count();

    List<PropertyImage> findByPropertyIdAndIsActiveTrueOrderByDisplayOrderAsc(UUID propertyId);
}
