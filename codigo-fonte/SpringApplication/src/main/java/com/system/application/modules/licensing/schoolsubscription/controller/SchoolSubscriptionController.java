package com.system.application.modules.licensing.schoolsubscription.controller;

import com.system.application.modules.licensing.schoolsubscription.dto.SchoolSubscriptionCheckoutResponse;
import com.system.application.modules.licensing.schoolsubscription.dto.SchoolSubscriptionDetailResponse;
import com.system.application.modules.licensing.schoolsubscription.dto.SchoolSubscriptionRequest;
import com.system.application.modules.licensing.schoolsubscription.dto.SchoolSubscriptionResponse;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/school-subscriptions")
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
    public ResponseEntity<SchoolSubscriptionDetailResponse> findById(
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
    public ResponseEntity<SchoolSubscriptionCheckoutResponse> paySubscription(
            @RequestBody @Valid SchoolSubscriptionRequest request,
            JwtAuthenticationToken token
    ) {
        UUID user = UUID.fromString(token.getName());
        SchoolSubscriptionCheckoutResponse checkout =
                schoolSubscriptionService.create(user, request);
        return ResponseEntity.ok(checkout);
    }

    @PutMapping("/{id}/cancel")
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
