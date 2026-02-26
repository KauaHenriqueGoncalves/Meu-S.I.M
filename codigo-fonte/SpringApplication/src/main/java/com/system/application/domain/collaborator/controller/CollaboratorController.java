package com.system.application.domain.collaborator.controller;

import com.system.application.domain.collaborator.dto.*;
import com.system.application.domain.collaborator.service.CollaboratorService;
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
public class CollaboratorController {
    private final UserMapper userMapper;
    private final CollaboratorService collaboratorService;

    public CollaboratorController(UserMapper userMapper,
                                  CollaboratorService collaboratorService) {
        this.userMapper = userMapper;
        this.collaboratorService = collaboratorService;
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @GetMapping("/collaborator/{id}")
    public ResponseEntity<CollaboratorDetailResponse> findByIdCollaborator(@PathVariable UUID id) {
        CollaboratorDetailResponse collaborator = collaboratorService.findById(id);
        return ResponseEntity.ok(collaborator);
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @GetMapping("/collaborators")
    public ResponseEntity<PageResponse<CollaboratorResponse>> findAllCollaboratorResponse(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                          @RequestParam(value = "size", defaultValue = "3") int size,
                                                                                          JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        Page<CollaboratorResponse> collaboratorPage = collaboratorService.findAllBySchoolAdminId(adminId, PageRequest.of(page, size));
        PageResponse<CollaboratorResponse> response = new PageResponse<>(
                collaboratorPage.getContent(),
                collaboratorPage.getNumber(),
                collaboratorPage.getSize(),
                collaboratorPage.getTotalPages(),
                collaboratorPage.getTotalElements(),
                collaboratorPage.hasNext(),
                collaboratorPage.hasPrevious()
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PostMapping("/collaborator")
    public ResponseEntity<Void> createCollaborator(@RequestBody @Valid CreateCollaboratorRequest createCollaboratorRequest,
                                                   JwtAuthenticationToken jwtToken) {
        User user = userMapper.toEntity(createCollaboratorRequest.userRequest());
        UUID adminId = UUID.fromString(jwtToken.getName());
        CollaboratorRequest request = createCollaboratorRequest.collaboratorRequest();
        UUID collaboratorId = collaboratorService.saveCollaborator(user, adminId, request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(collaboratorId)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PutMapping("/collaborator/{id}")
    public ResponseEntity<Void> updateCollaborator(@PathVariable("id") UUID collaboratorId,
                                                   @RequestBody @Valid UpdateCollaboratorRequest updateCollaborator,
                                                   JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        collaboratorService.updateCollaborator(adminId, collaboratorId, updateCollaborator);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PutMapping("/collaborator/password/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable("id") UUID collaboratorId,
                                               @RequestBody @Valid UpdateCollaboratorPasswordRequest updatePasswordRequest,
                                               JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        collaboratorService.updatePassword(adminId, collaboratorId, updatePasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @DeleteMapping("/collaborator/{id}")
    public ResponseEntity<Void> deleteByIdCollaborator(@PathVariable("id") UUID collaboratorId,
                                                       JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        collaboratorService.deleteById(adminId, collaboratorId);
        return ResponseEntity.noContent().build();
    }
}
