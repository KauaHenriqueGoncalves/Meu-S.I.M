package com.system.application.domain.user.listener;

import com.system.application.domain.user.User;
import com.system.application.domain.user.dto.UserRegisteredEvent;
import com.system.application.domain.user.repository.UserRepository;
import com.system.application.shared.email.service.EmailSendService;
import com.system.application.shared.email.service.EmailVerificationService;
import com.system.application.shared.exception.NotFoundObjectException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredListener {
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final EmailSendService emailSendService;

    public UserRegisteredListener(
            UserRepository userRepository,
            EmailVerificationService emailVerificationService,
            EmailSendService emailSendService
    ) {
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
        this.emailSendService = emailSendService;
    }

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        User user = userRepository.findById(event.userId())
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou o usuário"));
        String token = emailVerificationService.createOrRefreshToken(user.getId());
        String link = "http://localhost:8080/auth/verify?token=" + token;
        emailSendService.sendConfirmAccountEmail(user.getEmail(), user.getUsername(), link);
    }
}
