package com.system.application.core.schoolsubscription.controller;

import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionRequest;
import com.system.application.core.schoolsubscription.service.SchoolSubscriptionService;
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

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> paySubscription(
            @RequestBody @Valid SchoolSubscriptionRequest request,
            JwtAuthenticationToken token
    ) {
        // TODO: Teste

        UUID user = UUID.fromString(token.getName());
        schoolSubscriptionService.save(user, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> cancelSubscription(
            @PathVariable("id") UUID subscriptionId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        schoolSubscriptionService.cancelById(userId, subscriptionId);
        return ResponseEntity.ok().build();
    }
}
