package com.meusim.application.modules.identity.profile.collaborator.controller;

import com.meusim.application.modules.identity.profile.collaborator.Collaborator;
import com.meusim.application.modules.identity.profile.collaborator.dto.*;
import com.meusim.application.modules.identity.profile.collaborator.service.CollaboratorService;
import com.meusim.application.modules.identity.base.user.dto.PasswordRequest;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.meusim.application.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/collaborators")
public class CollaboratorController {
    private final CollaboratorService collaboratorService;

    public CollaboratorController(
            CollaboratorService collaboratorService
    ) {
        this.collaboratorService = collaboratorService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<PageResponse<CollaboratorResponse>> findAllBySchool(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size,
            @RequestParam(value = "name", defaultValue = "") String name,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        PageResponse<CollaboratorResponse> response =
                collaboratorService.findAllResponseBySchool(userId, name, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<CollaboratorDetailResponse> findResponseDetailById(
            @PathVariable UUID id
    ) {
        CollaboratorDetailResponse collaborator =
                collaboratorService.findResponseDetailById(id);
        return ResponseEntity.ok(collaborator);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> create(
            @RequestBody @Valid CreateCollaboratorRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        CreateUserRequestDTO createUserRequestDTO = request.createUserRequestDTO();
        CollaboratorRequest collaboratorRequest = request.collaboratorRequest();
        Collaborator collaborator =
                collaboratorService.save(userId, createUserRequestDTO, collaboratorRequest);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(collaborator.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> update(
            @PathVariable("id") UUID collaboratorId,
            @RequestBody @Valid UpdateCollaboratorRequest updateRequest,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        collaboratorService.update(userId, collaboratorId, updateRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> updatePassword(
            @PathVariable("id") UUID collaboratorId,
            @RequestBody @Valid PasswordRequest passwordRequest,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        collaboratorService.updatePassword(userId, collaboratorId, passwordRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") UUID collaboratorId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        collaboratorService.deleteById(userId, collaboratorId);
        return ResponseEntity.noContent().build();
    }
}
