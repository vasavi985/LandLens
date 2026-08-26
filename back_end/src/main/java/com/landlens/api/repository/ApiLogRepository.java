package com.landlens.api.repository;

import com.landlens.api.model.ApiLog;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiLogRepository {
    Optional<ApiLog> findById(UUID id);
    List<ApiLog> findAll();
    ApiLog save(ApiLog apiLog);
    void delete(ApiLog apiLog);
    void deleteById(UUID id);
    long count();

    List<ApiLog> findByApiKeyId(UUID apiKeyId);
}
