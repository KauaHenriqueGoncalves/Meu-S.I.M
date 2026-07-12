package com.system.application.auth.verification.scheduler;

import com.system.application.auth.verification.repository.EmailVerificationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class EmailVerificationTokenScheduler {
    private final static Logger log = LoggerFactory.getLogger(EmailVerificationTokenScheduler.class);

    private final EmailVerificationRepository emailVerificationRepository;
    private static final Duration GRACE_PERIOD = Duration.ofMinutes(15);

    public EmailVerificationTokenScheduler(EmailVerificationRepository emailVerificationRepository) {
        this.emailVerificationRepository = emailVerificationRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0/2 * * *")
    public void deleteAllTokenExpired() {
        log.info("Iniciando o agendamento de deletação de tokens de verificação");
        Instant threshold = Instant.now().minus(GRACE_PERIOD);
        int deletedCount = emailVerificationRepository.deleteAllExpiredBefore(threshold);
        if (deletedCount == 0) {
            log.info("Nenhum token de verificação expirado encontrado, processo encerrado!");
            return;
        }
        log.info("Total de tokens deletados: [total={}]", deletedCount);
    }
}
