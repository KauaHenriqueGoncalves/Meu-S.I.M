package com.system.application.domain.subject.controller;

import com.system.application.domain.subject.dto.SubjectRequest;
import com.system.application.domain.subject.dto.SubjectResponse;
import com.system.application.domain.subject.service.SubjectService;
import com.system.application.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
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

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @GetMapping("/f")
    public ResponseEntity<PageResponse<SubjectResponse>> findAllBySchool(@RequestParam(value = "page", defaultValue = "0") int page,
                                                @RequestParam(value = "size", defaultValue = "3") int size,
                                                JwtAuthenticationToken jwtToken) {
        UUID userId = UUID.fromString(jwtToken.getName());
        PageResponse<SubjectResponse> pageResponse = subjectService.findAllBySchool(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(pageResponse);
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid SubjectRequest subjectRequest,
                                     JwtAuthenticationToken jwtToken) {
        UUID userId = UUID.fromString(jwtToken.getName());
        UUID subjectId = subjectService.save(userId, subjectRequest);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(subjectId)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") UUID subjectId,
                                       @RequestBody @Valid SubjectRequest request,
                                       JwtAuthenticationToken jwtToken) {
        UUID userId = UUID.fromString(jwtToken.getName());
        subjectService.update(userId, subjectId, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID subjectId,
                                           JwtAuthenticationToken jwtToken) {
        UUID userId = UUID.fromString(jwtToken.getName());
        subjectService.deleteById(userId, subjectId);
        return ResponseEntity.noContent().build();
    }
}
