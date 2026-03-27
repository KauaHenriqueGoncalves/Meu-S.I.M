package com.system.application.modules.licensing.billingdiscount.controller;

import com.system.application.modules.licensing.billingdiscount.BillingDiscount;
import com.system.application.modules.licensing.billingdiscount.dto.BillingDiscountRequest;
import com.system.application.modules.licensing.billingdiscount.dto.BillingDiscountResponse;
import com.system.application.modules.licensing.billingdiscount.service.BillingDiscountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/billing-discounts")
public class BillingDiscountController {
    private final BillingDiscountService billingDiscountService;

    public BillingDiscountController(
            BillingDiscountService billingDiscountService
    ) {
        this.billingDiscountService = billingDiscountService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<List<BillingDiscountResponse>> findAll() {
        List<BillingDiscountResponse> response =
                billingDiscountService.findAll();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> save(
            @RequestBody @Valid BillingDiscountRequest billingDiscountRequest
    ) {
        BillingDiscount billingDiscount =
                billingDiscountService.save(billingDiscountRequest);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(billingDiscount.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> update(
            @PathVariable UUID id,
            @RequestBody @Valid BillingDiscountRequest billingDiscountRequest
    ) {
       billingDiscountService.update(id, billingDiscountRequest);
       return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        billingDiscountService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
