package com.landlens.auth.repository;

import com.landlens.auth.model.RefreshToken;
import com.landlens.user.model.User;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findById(UUID id);
    List<RefreshToken> findAll();
    RefreshToken save(RefreshToken refreshToken);
    void delete(RefreshToken refreshToken);
    void deleteById(UUID id);
    long count();

    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
