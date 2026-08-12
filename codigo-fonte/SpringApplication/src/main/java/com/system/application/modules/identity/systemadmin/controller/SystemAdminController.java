package com.system.application.modules.identity.systemadmin.controller;

import com.system.application.modules.identity.systemadmin.SystemAdmin;
import com.system.application.modules.identity.systemadmin.dto.SystemAdminDetailViewResponseDTO;
import com.system.application.modules.identity.systemadmin.dto.SystemAdminSimpleViewResponseDTO;
import com.system.application.modules.identity.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.system.application.modules.identity.systemadmin.service.SystemAdminService;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/system-admin")
public class SystemAdminController {
    private final SystemAdminService systemAdminService;

    public SystemAdminController(SystemAdminService systemAdminService) {
        this.systemAdminService = systemAdminService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<List<SystemAdminSimpleViewResponseDTO>> findAll(JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        List<SystemAdmin> admins = systemAdminService.findAll(userId);
        List<SystemAdminSimpleViewResponseDTO> response = SystemAdminSimpleViewResponseDTO.of(admins);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<SystemAdminDetailViewResponseDTO> findById(
            @PathVariable UUID id,
            JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        SystemAdmin admin = systemAdminService.findByIdWithCache(userId, id);
        SystemAdminDetailViewResponseDTO response = SystemAdminDetailViewResponseDTO.of(admin);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> create(
            @RequestBody UserRequest request,
            JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        systemAdminService.save(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> update(
            @PathVariable UUID id,
            @RequestBody UpdateSystemAdminRequestDTO request,
            JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        systemAdminService.update(userId, id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> updatePassword(
            @PathVariable UUID id,
            @RequestBody PasswordRequest request,
            JwtAuthenticationToken token){
        UUID userId = UUID.fromString(token.getName());
        systemAdminService.updatePassword(userId, id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        systemAdminService.deleteById(userId, id);
        return ResponseEntity.noContent().build();
    }
}
