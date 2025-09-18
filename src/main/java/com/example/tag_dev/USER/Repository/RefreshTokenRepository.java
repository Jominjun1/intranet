package com.example.tag_dev.USER.Repository;

import com.example.tag_dev.USER.Model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByRefreshToken(String refreshToken);
    void deleteByUserId(Long userId);
    void deleteAllByRefreshToken(String refreshToken);
}
