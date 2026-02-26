package com.system.application.shared.email.service;

import com.system.application.domain.user.User;
import com.system.application.domain.user.repository.UserRepository;
import com.system.application.shared.email.EmailVerificationToken;
import com.system.application.shared.email.repository.EmailVerificationRepository;
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
    private final UserRepository userRepository;

    public EmailVerificationServiceImpl(
            EmailVerificationRepository emailVerificationRepository,
            UserRepository userRepository
    ) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.userRepository = userRepository;
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
        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new NotFoundObjectException("Usuario não existe no token"));
        user.setActive(true);
        userRepository.save(user);
        emailVerificationRepository.deleteById(verificationToken.getId());
    }
}
