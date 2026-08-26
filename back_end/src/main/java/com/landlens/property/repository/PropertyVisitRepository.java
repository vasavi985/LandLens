package com.landlens.property.repository;

import com.landlens.property.model.PropertyVisit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyVisitRepository {
    Optional<PropertyVisit> findById(UUID id);
    List<PropertyVisit> findAll();
    PropertyVisit save(PropertyVisit propertyVisit);
    void delete(PropertyVisit propertyVisit);
    void deleteById(UUID id);
    long count();

    List<PropertyVisit> findByBuyerIdAndIsActiveTrue(UUID buyerId);
    List<PropertyVisit> findByPropertyProviderIdAndIsActiveTrue(UUID providerId);
}
