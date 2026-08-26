package com.landlens.property.repository;

import com.landlens.property.model.Property;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository {
    Optional<Property> findById(UUID id);
    List<Property> findAll();
    Property save(Property property);
    void delete(Property property);
    void deleteById(UUID id);
    long count();

    Optional<Property> findByPropertyCode(String propertyCode);
    boolean existsByPropertyCode(String propertyCode);
    List<Property> findByProviderIdAndIsActiveTrue(UUID providerId);
    List<Property> findByIsActiveTrueOrderByCreatedAtAsc();

    List<Property> searchProperties(
            String district,
            String village,
            String state,
            String status,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice);
}
