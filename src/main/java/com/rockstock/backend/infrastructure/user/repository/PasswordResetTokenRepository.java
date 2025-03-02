package com.rockstock.backend.infrastructure.user.repository;

import com.rockstock.backend.entity.user.PasswordResetToken;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.user.id = :userId")
    void deleteByUser_Id(@Param("userId") Long userId);

    Optional<PasswordResetToken> findByToken(String token);
}
