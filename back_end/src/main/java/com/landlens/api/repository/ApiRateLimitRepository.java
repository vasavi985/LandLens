package com.landlens.api.repository;

import com.landlens.api.model.ApiRateLimit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiRateLimitRepository {
    Optional<ApiRateLimit> findById(UUID id);
    List<ApiRateLimit> findAll();
    ApiRateLimit save(ApiRateLimit apiRateLimit);
    void delete(ApiRateLimit apiRateLimit);
    void deleteById(UUID id);
    long count();

    Optional<ApiRateLimit> findByApiKeyId(UUID apiKeyId);
}
