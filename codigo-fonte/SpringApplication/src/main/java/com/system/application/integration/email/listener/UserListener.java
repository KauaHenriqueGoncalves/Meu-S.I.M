package com.system.application.integration.email.listener;

import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.event.UserRegisteredEvent;
import com.system.application.modules.identity.user.service.UserService;
import com.system.application.integration.email.service.EmailSendService;
import com.system.application.auth.verification.service.EmailVerificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserListener {
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

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        User user = userService.findById(event.userId());
        String token = emailVerificationService.createOrRefreshToken(user.getId());
        String link = "http://localhost:8080/api/v1/auth/verify?token=" + token;
        emailSendService.sendConfirmAccountEmail(user.getEmail(), user.getUsername(), link);
    }
}
