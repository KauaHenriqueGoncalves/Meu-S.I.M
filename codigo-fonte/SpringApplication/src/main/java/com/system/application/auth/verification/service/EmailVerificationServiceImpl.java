package com.system.application.auth.verification.service;

import com.system.application.modules.identity.user.service.UserService;
import com.system.application.auth.verification.EmailVerificationToken;
import com.system.application.auth.verification.repository.EmailVerificationRepository;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService{
    private static final Logger log =
            LoggerFactory.getLogger(EmailVerificationServiceImpl.class);

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String createOrRefreshToken(UUID userId) {
        log.info("Criando ou renovando token de verificacao de e-mail. [userId={}]", userId);

        Optional<EmailVerificationToken> existing =
                emailVerificationRepository.findByUserId(userId);

        if (existing.isPresent()) {
            log.info("Token de verificacao ja existente, reutilizando. [userId={}]", userId);
            return existing.get().getToken();
        }

        EmailVerificationToken verificationToken = new EmailVerificationToken(
                null,
                UUID.randomUUID().toString(),
                Instant.now().plusSeconds(900),
                userId
        );
        verificationToken = emailVerificationRepository.save(verificationToken);

        log.info("Token de verificacao criado com sucesso. [userId={}] [expiresAt={}]",
                userId, verificationToken.getExpiresAt());

        return verificationToken.getToken();
    }

    @Override
    @Transactional
    public void validateUser(String token) throws NotFoundObjectException, AccessDeniedException {
        log.info("Iniciando validacao de token de verificacao de e-mail.");

        EmailVerificationToken verificationToken = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Token de verificacao invalido ou nao encontrado.");
                    return new NotFoundObjectException("Token invalido");
                });

        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
            log.warn("Token de verificacao expirado. [userId={}] [expiresAt={}]",
                    verificationToken.getUserId(), verificationToken.getExpiresAt());
            throw new AccessDeniedException("Token expirado");
        }

        userService.activateUser(verificationToken.getUserId());
        emailVerificationRepository.deleteById(verificationToken.getId());

        log.info("E-mail verificado e usuario ativado com sucesso. [userId={}]",
                verificationToken.getUserId());
    }
}
