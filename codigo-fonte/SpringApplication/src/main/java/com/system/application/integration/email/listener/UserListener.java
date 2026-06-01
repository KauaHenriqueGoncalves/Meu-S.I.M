package com.system.application.integration.email.listener;

import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.event.UserRegisteredEvent;
import com.system.application.modules.identity.user.service.UserService;
import com.system.application.integration.email.service.EmailSendService;
import com.system.application.auth.verification.service.EmailVerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserListener {
    private static final Logger log =
            LoggerFactory.getLogger(UserListener.class);

    @Value("${api.v1.url.back-end}")
    private String backendUrl;

    private final EmailVerificationService emailVerificationService;
    private final EmailSendService emailSendService;
    private final UserService userService;

    public UserListener(
            EmailVerificationService emailVerificationService,
            EmailSendService emailSendService,
            UserService userService
    ) {
        this.emailVerificationService = emailVerificationService;
        this.emailSendService = emailSendService;
        this.userService = userService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Evento de registro de usuario recebido. [userId={}]", event.userId());

        User user = userService.findById(event.userId());
        String token = emailVerificationService.createOrRefreshToken(user.getId());
        String link = backendUrl + "/auth/verify?token=" + token;

        emailSendService.sendConfirmAccountEmail(user.getEmail(), user.getUsername(), link);

        log.info("E-mail de confirmacao de conta enviado. [userId={}] [email={}]",
                user.getId(), user.getEmail());
    }
}
