package com.landlens.user.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.common.AbstractFirestoreRepository;
import com.landlens.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class FirestoreUserRepository extends AbstractFirestoreRepository<User> implements UserRepository {

    public FirestoreUserRepository() {
        super("users", User.class);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        try {
            QuerySnapshot snapshot = getCollection().whereEqualTo("email", email).limit(1).get().get();
            if (!snapshot.isEmpty()) {
                return Optional.ofNullable(mapToObject(snapshot.getDocuments().get(0).getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error searching user by email: " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public List<User> findByRoleName(String roleName) {
        if (roleName == null) return List.of();
        try {
            QuerySnapshot snapshot = getCollection().whereEqualTo("role.name", roleName.toUpperCase()).get().get();
            return snapshot.getDocuments().stream()
                    .map(doc -> mapToObject(doc.getData()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error searching users by role: " + roleName, e);
        }
    }
}
