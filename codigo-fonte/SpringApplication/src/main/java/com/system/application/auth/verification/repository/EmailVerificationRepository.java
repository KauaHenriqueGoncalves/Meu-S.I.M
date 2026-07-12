package com.system.application.auth.verification.repository;

import com.system.application.auth.verification.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationToken, UUID> {
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken evk WHERE evk.expiresAt <= :threshold")
    int deleteAllExpiredBefore(@Param("threshold") Instant threshold);
}
