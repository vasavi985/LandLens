package com.landlens.fraud.repository;

import com.landlens.fraud.model.FraudReport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FraudReportRepository {
    Optional<FraudReport> findById(UUID id);
    List<FraudReport> findAll();
    FraudReport save(FraudReport fraudReport);
    void delete(FraudReport fraudReport);
    void deleteById(UUID id);
    long count();

    List<FraudReport> findByPropertyId(UUID propertyId);
    List<FraudReport> findByStatus(String status);
    List<FraudReport> findByOfficerId(UUID officerId);
}
