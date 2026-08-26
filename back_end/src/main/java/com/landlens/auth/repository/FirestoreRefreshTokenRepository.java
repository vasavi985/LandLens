package com.landlens.auth.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.landlens.auth.model.RefreshToken;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FirestoreRefreshTokenRepository extends AbstractFirestoreRepository<RefreshToken> implements RefreshTokenRepository {

    public FirestoreRefreshTokenRepository() {
        super("refresh_tokens", RefreshToken.class);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        if (token == null) return Optional.empty();
        try {
            QuerySnapshot snapshot = getCollection().whereEqualTo("token", token).limit(1).get().get();
            if (!snapshot.isEmpty()) {
                return Optional.ofNullable(mapToObject(snapshot.getDocuments().get(0).getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error searching refresh token: " + token, e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteByUser(User user) {
        if (user == null || user.getId() == null) return;
        try {
            QuerySnapshot snapshot = getCollection().whereEqualTo("user.id", user.getId().toString()).get().get();
            if (!snapshot.isEmpty()) {
                WriteBatch batch = firestore.batch();
                snapshot.getDocuments().forEach(doc -> batch.delete(doc.getReference()));
                batch.commit().get();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting refresh tokens for user ID: " + user.getId(), e);
        }
    }
}
