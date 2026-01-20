package com.system.application.domain.legalGuardian.controller;

import com.system.application.domain.legalGuardian.dto.CreateLegalGuardianRequest;
import com.system.application.domain.legalGuardian.dto.LegalGuardianRequest;
import com.system.application.domain.legalGuardian.service.LegalGuardianService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class LegalGuardianController {
    private final UserMapper userMapper;
    private final LegalGuardianService legalGuardianService;

    public LegalGuardianController(UserMapper userMapper,
                                   LegalGuardianService legalGuardianService) {
        this.userMapper = userMapper;
        this.legalGuardianService = legalGuardianService;
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PostMapping("/legal-guardian")
    public ResponseEntity<Void> createLegalGuardian(@RequestBody @Valid CreateLegalGuardianRequest legalGuardianRequest,
                                                    JwtAuthenticationToken jwtToken) {
        User user = userMapper.toEntity(legalGuardianRequest.userRequest());
        UUID adminId = UUID.fromString(jwtToken.getName());
        LegalGuardianRequest request = legalGuardianRequest.legalGuardianRequest();
        UUID legalGuardianId = legalGuardianService.saveLegalGuardian(user, adminId, request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(legalGuardianId)
                .toUri();
        return ResponseEntity.created(uri).build();
    }
}
