package com.system.application.modules.identity.schooladmin.controller;

import com.system.application.integration.captcha.service.CaptchaService;
import com.system.application.modules.identity.schooladmin.SchoolAdmin;
import com.system.application.modules.identity.schooladmin.dto.CreateSchoolAdminRequest;
import com.system.application.modules.identity.schooladmin.service.SchoolAdminService;
import com.system.application.shared.exception.AccessDeniedException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/school-admins")
public class SchoolAdminController {
    private final SchoolAdminService schoolAdminService;
    private final CaptchaService captchaService;

    public SchoolAdminController(
            SchoolAdminService schoolAdminService,
            @Qualifier("turnstile") CaptchaService captchaService
    ) {
        this.schoolAdminService = schoolAdminService;
        this.captchaService = captchaService;
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody @Valid CreateSchoolAdminRequest request
    ) {
        if (!captchaService.validate(request.captchaRequest().captchaToken())) {
            throw new AccessDeniedException("Verificação de segurança falhou!");
        }

        SchoolAdmin schoolAdmin =
                schoolAdminService.save(request.userRequest(), request.schoolRequest());
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(schoolAdmin.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }
}
