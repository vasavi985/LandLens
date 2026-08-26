package com.landlens.api.repository;

import com.landlens.api.model.ApiUsage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiUsageRepository {
    Optional<ApiUsage> findById(UUID id);
    List<ApiUsage> findAll();
    ApiUsage save(ApiUsage apiUsage);
    void delete(ApiUsage apiUsage);
    void deleteById(UUID id);
    long count();

    Optional<ApiUsage> findByApiKeyIdAndUsageDate(UUID apiKeyId, LocalDate usageDate);
}
