package com.system.application.email.controller;

import com.system.application.email.service.EmailVerificationService;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class EmailVerificationController {
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
        catch (NotFoundObjectException | AccessDeniedException e) {
            return ResponseEntity.ok("Token Invalido!"); // Redirecionar para uma pagina de invalid
        }
        return ResponseEntity.ok("Perfil validado com sucesso!"); // Redirecionar para uma pagina de sucesso
    }
}
