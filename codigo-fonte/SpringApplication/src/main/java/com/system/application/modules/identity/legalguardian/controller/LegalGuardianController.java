package com.system.application.modules.identity.legalguardian.controller;

import com.system.application.modules.identity.legalguardian.LegalGuardian;
import com.system.application.modules.identity.legalguardian.dto.*;
import com.system.application.modules.identity.legalguardian.service.LegalGuardianService;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import com.system.application.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/legal-guardians")
public class LegalGuardianController {
    private final LegalGuardianService legalGuardianService;

    public LegalGuardianController(
            LegalGuardianService legalGuardianService
    ) {
        this.legalGuardianService = legalGuardianService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<PageResponse<LegalGuardianResponse>> findAllResponse(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        PageResponse<LegalGuardianResponse> response =
                legalGuardianService.findAllResponseBySchool(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<LegalGuardianDetailResponse> findResponseDetailById(
            @PathVariable("id") UUID legalGuardianId
    ) {
        LegalGuardianDetailResponse legalGuardian =
                legalGuardianService.findResponseDetailById(legalGuardianId);
        return ResponseEntity.ok(legalGuardian);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> create(
            @RequestBody @Valid CreateLegalGuardianRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        UserRequest userRequest = request.userRequest();
        LegalGuardianRequest legalGuardianRequest = request.legalGuardianRequest();
        LegalGuardian legalGuardian =
                legalGuardianService.save(userId, userRequest, legalGuardianRequest);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(legalGuardian.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> update(
            @PathVariable("id") UUID legalGuardianId,
            @RequestBody @Valid UpdateLegalGuardianRequest updateRequest,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        legalGuardianService.update(userId, legalGuardianId, updateRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> updatePassword(
            @PathVariable("id") UUID legalGuardianId,
            @RequestBody @Valid PasswordRequest updatePassword,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        legalGuardianService.updatePassword(userId, legalGuardianId, updatePassword);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") UUID legalGuardianId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        legalGuardianService.deleteById(userId, legalGuardianId);
        return ResponseEntity.noContent().build();
    }
}
