package com.system.application.domain.subject.controller;

import com.system.application.domain.subject.Subject;
import com.system.application.domain.subject.dto.SubjectRequest;
import com.system.application.domain.subject.dto.SubjectResponse;
import com.system.application.domain.subject.service.SubjectService;
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
@RequestMapping("/subject")
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(
            SubjectService subjectService
    ) {
        this.subjectService = subjectService;
    }

    @GetMapping("/f")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<PageResponse<SubjectResponse>> findAllBySchool(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        PageResponse<SubjectResponse> pageResponse =
                subjectService.findAllResponseBySchool(userId, page, size);
        return ResponseEntity.ok(pageResponse);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> save(
            @RequestBody @Valid SubjectRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        Subject subject = subjectService.save(userId, request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(subject.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> update(
            @PathVariable("id") UUID subjectId,
            @RequestBody @Valid SubjectRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        subjectService.update(userId, subjectId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") UUID subjectId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        subjectService.deleteById(userId, subjectId);
        return ResponseEntity.noContent().build();
    }
}
