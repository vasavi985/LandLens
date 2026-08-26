package com.landlens.api.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.api.model.ApiRateLimit;
import com.landlens.common.AbstractFirestoreRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class FirestoreApiRateLimitRepository extends AbstractFirestoreRepository<ApiRateLimit> implements ApiRateLimitRepository {

    public FirestoreApiRateLimitRepository() {
        super("api_rate_limits", ApiRateLimit.class);
    }

    @Override
    public Optional<ApiRateLimit> findByApiKeyId(UUID apiKeyId) {
        if (apiKeyId == null) return Optional.empty();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("apiKey.id", apiKeyId.toString())
                    .limit(1).get().get();
            if (!snapshot.isEmpty()) {
                return Optional.ofNullable(mapToObject(snapshot.getDocuments().get(0).getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error searching API rate limit by apiKey: " + apiKeyId, e);
        }
        return Optional.empty();
    }
}
