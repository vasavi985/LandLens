package com.landlens.property.repository;

import com.landlens.property.model.SavedProperty;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedPropertyRepository {
    Optional<SavedProperty> findById(UUID id);
    List<SavedProperty> findAll();
    SavedProperty save(SavedProperty savedProperty);
    void delete(SavedProperty savedProperty);
    void deleteById(UUID id);
    long count();

    List<SavedProperty> findByBuyerIdAndIsActiveTrue(UUID buyerId);
    Optional<SavedProperty> findByBuyerIdAndPropertyIdAndIsActiveTrue(UUID buyerId, UUID propertyId);
}
