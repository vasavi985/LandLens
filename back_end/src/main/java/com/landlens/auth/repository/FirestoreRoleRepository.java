package com.landlens.auth.repository;

import com.google.cloud.firestore.QuerySnapshot;
import com.landlens.auth.model.Role;
import com.landlens.common.AbstractFirestoreRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FirestoreRoleRepository extends AbstractFirestoreRepository<Role> implements RoleRepository {

    public FirestoreRoleRepository() {
        super("roles", Role.class);
    }

    @Override
    public Optional<Role> findByName(String name) {
        if (name == null) return Optional.empty();
        try {
            QuerySnapshot snapshot = getCollection().whereEqualTo("name", name.toUpperCase()).limit(1).get().get();
            if (!snapshot.isEmpty()) {
                return Optional.ofNullable(mapToObject(snapshot.getDocuments().get(0).getData()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error searching role by name: " + name, e);
        }
        return Optional.empty();
    }
}
