package com.landlens.api.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.api.model.ApiKey;
import com.landlens.common.AbstractFirestoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreApiKeyRepository extends AbstractFirestoreRepository<ApiKey> implements ApiKeyRepository {

    public FirestoreApiKeyRepository() {
        super("api_keys", ApiKey.class);
    }

    @Override
    public Optional<ApiKey> findByKeyHashAndStatus(String keyHash, String status) {
        if (keyHash == null || status == null) return Optional.empty();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("keyHash", keyHash)
                    .whereEqualTo("status", status.toUpperCase())
                    .limit(1).get().get();
            if (!snapshot.isEmpty()) {
                return Optional.ofNullable(mapToObject(snapshot.getDocuments().get(0).getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error searching API key by hash and status", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ApiKey> findByUserIdAndStatus(UUID userId, String status) {
        if (userId == null || status == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("user.id", userId.toString())
                    .whereEqualTo("status", status.toUpperCase())
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching API keys by user and status", e);
        }
    }
}
