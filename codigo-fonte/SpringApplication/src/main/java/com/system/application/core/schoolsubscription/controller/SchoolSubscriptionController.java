package com.system.application.core.schoolsubscription.controller;

import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionDetailResponse;
import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionRequest;
import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionResponse;
import com.system.application.core.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/school-subscription")
public class SchoolSubscriptionController {
    private final SchoolSubscriptionService schoolSubscriptionService;

    public SchoolSubscriptionController(
            SchoolSubscriptionService schoolSubscriptionService
    ) {
        this.schoolSubscriptionService = schoolSubscriptionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<PageResponse<SchoolSubscriptionResponse>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        PageResponse<SchoolSubscriptionResponse> response =
                schoolSubscriptionService.findAllResponseBySchoolId(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<SchoolSubscriptionDetailResponse> findDetailById(
            @PathVariable("id") UUID subscriptionId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        SchoolSubscriptionDetailResponse response =
                schoolSubscriptionService.findDetailById(userId, subscriptionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> paySubscription(
            @RequestBody @Valid SchoolSubscriptionRequest request,
            JwtAuthenticationToken token
    ) {
        // TODO: Teste

        UUID user = UUID.fromString(token.getName());
        schoolSubscriptionService.create(user, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/active/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> activeSubscription(
            @PathVariable("id") UUID subscriptionId,
            JwtAuthenticationToken token
    ) {

        //TODO: Teste

        UUID userId = UUID.fromString(token.getName());
        schoolSubscriptionService.ActiveById(userId, subscriptionId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> cancelSubscription(
            @PathVariable("id") UUID subscriptionId,
            JwtAuthenticationToken token
    ) {

        //TODO: Teste

        UUID userId = UUID.fromString(token.getName());
        schoolSubscriptionService.cancelById(userId, subscriptionId);
        return ResponseEntity.ok().build();
    }
}
