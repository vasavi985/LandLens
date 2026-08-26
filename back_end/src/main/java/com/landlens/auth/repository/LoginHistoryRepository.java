package com.landlens.auth.repository;

import com.landlens.auth.model.LoginHistory;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface LoginHistoryRepository {
    Optional<LoginHistory> findById(UUID id);
    List<LoginHistory> findAll();
    LoginHistory save(LoginHistory loginHistory);
    void delete(LoginHistory loginHistory);
    void deleteById(UUID id);
    long count();

    List<LoginHistory> findByUserId(UUID userId);
}
