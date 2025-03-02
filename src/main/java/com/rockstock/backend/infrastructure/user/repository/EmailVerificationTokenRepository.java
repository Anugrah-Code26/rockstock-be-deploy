package com.rockstock.backend.infrastructure.user.repository;

import com.rockstock.backend.entity.user.EmailVerificationToken;
import com.rockstock.backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByUser_IdAndExpiryDateLessThan(Long userId, LocalDateTime expiryDate);
    void deleteByUser(User user);
}
