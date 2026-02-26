package com.system.application.domain.collaborator.controller;

import com.system.application.domain.collaborator.Collaborator;
import com.system.application.domain.collaborator.dto.*;
import com.system.application.domain.collaborator.service.CollaboratorService;
import com.system.application.domain.user.dto.UserRequest;
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
@RequestMapping("/users")
public class CollaboratorController {
    private final CollaboratorService collaboratorService;

    public CollaboratorController(
            CollaboratorService collaboratorService
    ) {
        this.collaboratorService = collaboratorService;
    }

    @GetMapping("/collaborators")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<PageResponse<CollaboratorResponse>> findAllBySchool(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        PageResponse<CollaboratorResponse> response =
                collaboratorService.findAllResponseBySchool(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/collaborator/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<CollaboratorDetailResponse> findResponseDetailById(
            @PathVariable UUID id
    ) {
        CollaboratorDetailResponse collaborator = collaboratorService.findResponseDetailById(id);
        return ResponseEntity.ok(collaborator);
    }

    @PostMapping("/collaborator")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> create(
            @RequestBody @Valid CreateCollaboratorRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        UserRequest userRequest = request.userRequest();
        CollaboratorRequest collaboratorRequest = request.collaboratorRequest();
        Collaborator collaborator = collaboratorService.save(userId, userRequest, collaboratorRequest);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(collaborator.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/collaborator/{id}")
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

    @PutMapping("/collaborator/password/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> updatePassword(
            @PathVariable("id") UUID collaboratorId,
            @RequestBody @Valid UpdateCollaboratorPasswordRequest passwordRequest,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        collaboratorService.updatePassword(userId, collaboratorId, passwordRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/collaborator/{id}")
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
