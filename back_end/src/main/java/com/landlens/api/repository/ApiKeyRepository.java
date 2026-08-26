package com.landlens.api.repository;

import com.landlens.api.model.ApiKey;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository {
    Optional<ApiKey> findById(UUID id);
    List<ApiKey> findAll();
    ApiKey save(ApiKey apiKey);
    void delete(ApiKey apiKey);
    void deleteById(UUID id);
    long count();

    Optional<ApiKey> findByKeyHashAndStatus(String keyHash, String status);
    List<ApiKey> findByUserIdAndStatus(UUID userId, String status);
}
