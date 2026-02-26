package com.system.application.email.service;

import com.system.application.domain.user.service.UserService;
import com.system.application.email.EmailVerificationToken;
import com.system.application.email.repository.EmailVerificationRepository;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService{
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserService userService;

    public EmailVerificationServiceImpl(
            EmailVerificationRepository emailVerificationRepository,
            UserService userService
    ) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public String createOrRefreshToken(UUID userId) {
        Optional<EmailVerificationToken> existing =
                emailVerificationRepository.findByUserId(userId);
        if (existing.isPresent()) {
            return existing.get().getToken();
        }
        EmailVerificationToken verificationToken = new EmailVerificationToken(
                null,
                UUID.randomUUID().toString(),
                Instant.now().plusSeconds(900),
                userId
        );
        verificationToken = emailVerificationRepository.save(verificationToken);
        return verificationToken.getToken();
    }

    @Override
    @Transactional
    public void validateUser(String token) throws NotFoundObjectException, AccessDeniedException {
        EmailVerificationToken verificationToken = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundObjectException("Token invalido"));
        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
            throw new AccessDeniedException("Token expirado");
        }
        userService.activateUser(verificationToken.getUserId());
        emailVerificationRepository.deleteById(verificationToken.getId());
    }
}
