package com.landlens.api.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.api.model.ApiUsage;
import com.landlens.common.AbstractFirestoreRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FirestoreApiUsageRepository extends AbstractFirestoreRepository<ApiUsage> implements ApiUsageRepository {

    public FirestoreApiUsageRepository() {
        super("api_usages", ApiUsage.class);
    }

    @Override
    public Optional<ApiUsage> findByApiKeyIdAndUsageDate(UUID apiKeyId, LocalDate usageDate) {
        if (apiKeyId == null || usageDate == null) return Optional.empty();
        try {
            QuerySnapshot snapshot = getCollection()
                    .whereEqualTo("apiKey.id", apiKeyId.toString())
                    .whereEqualTo("usageDate", usageDate.toString())
                    .limit(1).get().get();
            if (!snapshot.isEmpty()) {
                return Optional.ofNullable(mapToObject(snapshot.getDocuments().get(0).getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error searching API usage by apiKey and date", e);
        }
        return Optional.empty();
    }
}
