package com.landlens.api.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.api.model.ApiLog;
import com.landlens.common.AbstractFirestoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreApiLogRepository extends AbstractFirestoreRepository<ApiLog> implements ApiLogRepository {

    public FirestoreApiLogRepository() {
        super("api_logs", ApiLog.class);
    }

    @Override
    public List<ApiLog> findByApiKeyId(UUID apiKeyId) {
        if (apiKeyId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("apiKey.id", apiKeyId.toString())
                    .get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching API logs by apiKey: " + apiKeyId, e);
        }
    }
}
