package com.system.application.auth.verification.controller;

import com.system.application.auth.verification.service.EmailVerificationService;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class EmailVerificationController {
    @Value("${api.v1.url.front-end}")
    private String frontendUrl;

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(
            EmailVerificationService emailVerificationService
    ) {
        this.emailVerificationService = emailVerificationService;
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(
            @RequestParam("token") String token
    ) {
        try {
            emailVerificationService.validateUser(token);
        }
        catch (NotFoundObjectException e) {
            return ResponseEntity.status(302)
                    .header("Location", frontendUrl + "/auth/verify-account/failed?reason=invalid")
                    .build();
        }
        catch (AccessDeniedException e) {
            return ResponseEntity.status(302)
                    .header("Location", frontendUrl + "/auth/verify-account/failed?reason=expired")
                    .build();
        }
        return ResponseEntity.status(302)
                .header("Location", frontendUrl + "/auth/verify-account/success")
                .build();
    }
}
