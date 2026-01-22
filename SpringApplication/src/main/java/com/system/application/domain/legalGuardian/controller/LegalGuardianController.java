package com.system.application.domain.legalGuardian.controller;

import com.system.application.domain.legalGuardian.dto.*;
import com.system.application.domain.legalGuardian.service.LegalGuardianService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.mapper.UserMapper;
import com.system.application.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
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
    @GetMapping("/legal-guardians")
    public ResponseEntity<PageResponse<LegalGuardianResponse>> findAllLegalGuardianResponse(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                           @RequestParam(value = "size", defaultValue = "3") int size,
                                                                                           JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        Page<LegalGuardianResponse> legalGuardianPage = legalGuardianService.findAllBySchoolAdminId(adminId, PageRequest.of(page, size));
        PageResponse<LegalGuardianResponse> response = new PageResponse<>(
                legalGuardianPage.getContent(),
                legalGuardianPage.getNumber(),
                legalGuardianPage.getSize(),
                legalGuardianPage.getTotalPages(),
                legalGuardianPage.getTotalElements(),
                legalGuardianPage.hasNext(),
                legalGuardianPage.hasPrevious()
        );
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @GetMapping("/legal-guardian/{id}")
    public ResponseEntity<LegalGuardianDetailResponse> findByIdLegalGuardian(@PathVariable UUID id) {
        LegalGuardianDetailResponse legalGuardian = legalGuardianService.findById(id);
        return ResponseEntity.ok(legalGuardian);
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

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PutMapping("/legal-guardian/{id}")
    public ResponseEntity<Void> updateLegalGuardian(@PathVariable("id") UUID legalGuardianId,
                                                    @RequestBody @Valid UpdateLegalGuardianRequest updateLegalGuardian,
                                                    JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        legalGuardianService.updateLegalGuardian(adminId, legalGuardianId, updateLegalGuardian);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PutMapping("/legal-guardian/password/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable("id") UUID legalGuardianId,
                                                @RequestBody @Valid UpdateLegalGuardianPasswordRequest updatePassword,
                                                JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        legalGuardianService.updatePassword(adminId, legalGuardianId, updatePassword);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @DeleteMapping("/legal-guardian/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID legalGuardianId,
                                           JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        legalGuardianService.deleteById(adminId, legalGuardianId);
        return ResponseEntity.noContent().build();
    }
}
