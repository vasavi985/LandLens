package com.landlens.auth.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.auth.model.LoginHistory;
import com.landlens.common.AbstractFirestoreRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreLoginHistoryRepository extends AbstractFirestoreRepository<LoginHistory> implements LoginHistoryRepository {

    public FirestoreLoginHistoryRepository() {
        super("login_histories", LoginHistory.class);
    }

    @Override
    public List<LoginHistory> findByUserId(UUID userId) {
        if (userId == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection().whereEqualTo("user.id", userId.toString()).get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching login history for user: " + userId, e);
        }
    }
}
