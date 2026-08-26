package com.landlens.user.repository;

import com.landlens.user.model.User;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface UserRepository {
    Optional<User> findById(UUID id);
    List<User> findAll();
    User save(User user);
    void delete(User user);
    void deleteById(UUID id);
    long count();

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRoleName(String roleName);
}
