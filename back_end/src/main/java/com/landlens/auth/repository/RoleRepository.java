package com.landlens.auth.repository;

import com.landlens.auth.model.Role;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface RoleRepository {
    Optional<Role> findById(UUID id);
    List<Role> findAll();
    Role save(Role role);
    void delete(Role role);
    void deleteById(UUID id);
    long count();

    Optional<Role> findByName(String name);
}
