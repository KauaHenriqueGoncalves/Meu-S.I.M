package com.meusim.application.integration.email.listener;

import com.meusim.application.modules.identity.base.role.Role;
import com.meusim.application.modules.identity.base.user.User;
import com.meusim.application.modules.identity.base.user.event.UserRegisteredEvent;
import com.meusim.application.modules.identity.base.user.service.UserService;
import com.meusim.application.integration.email.service.EmailSendService;
import com.meusim.application.auth.verification.service.EmailVerificationService;
import com.meusim.application.modules.school.School;
import com.meusim.application.modules.school.service.SchoolService;
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
    private final SchoolService schoolService;

    public UserListener(
            EmailVerificationService emailVerificationService,
            EmailSendService emailSendService,
            UserService userService,
            SchoolService schoolService
    ) {
        this.emailVerificationService = emailVerificationService;
        this.emailSendService = emailSendService;
        this.userService = userService;
        this.schoolService = schoolService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Evento de registro de usuario recebido. [userId={}]", event.userId());
        User user = userService.findById(event.userId());
        School school = null;
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role ->(role.getId() == Role.Values.SYSTEM_ADMIN.getValue()));
        if (!isAdmin) {
            school = schoolService.findByUserId(user.getId());
        }
        String token = emailVerificationService.createOrRefreshToken(user.getId());
        String link = backendUrl + "/auth/verify?token=" + token;
        if (school == null) {
            emailSendService.sendConfirmAccountEmail(user.getEmail(), user.getUsername(), "Não possui código", link);
            log.info("E-mail enviado para conta de Admin. [userId={}] [email={}] [role={}]",
                    user.getId(), user.getEmail(), user.getRole());
        }
        else {
            emailSendService.sendConfirmAccountEmail(user.getEmail(), user.getUsername(), school.getNameCode(), link);
            log.info("E-mail enviado para conta de usuário da aplicação. [userId={}] [email={}] [role={}]",
                    user.getId(), user.getEmail(), user.getRole());
        }
        log.info("E-mail de confirmacao de conta enviado. [userId={}] [email={}]",
                user.getId(), user.getEmail());
    }
}
