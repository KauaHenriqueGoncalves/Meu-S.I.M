package com.system.application.modules.licensing.schoolplan.controller;

import com.system.application.modules.licensing.schoolplan.SchoolPlan;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanRequest;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanResponse;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanSimpleResponse;
import com.system.application.modules.licensing.schoolplan.dto.UpdateSchoolPlanRequest;
import com.system.application.modules.licensing.schoolplan.service.SchoolPlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/school-plans")
public class SchoolPlanController {
    private final SchoolPlanService schoolPlanService;

    public SchoolPlanController(
            SchoolPlanService schoolPlanService
    ) {
        this.schoolPlanService = schoolPlanService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<List<SchoolPlanResponse>> findAll() {
        List<SchoolPlanResponse> schoolPlans =
                schoolPlanService.findAll();
        return ResponseEntity.ok(schoolPlans);
    }

    @GetMapping("/to-client")
    @PreAuthorize("hasAnyAuthority('SCOPE_system_admin', 'SCOPE_school_admin')")
    public ResponseEntity<List<SchoolPlanSimpleResponse>> findAllToClient() {
        List<SchoolPlanSimpleResponse> schoolPlans = schoolPlanService.findAllSimple();
        return ResponseEntity.ok(schoolPlans);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> save(
            @RequestBody @Valid SchoolPlanRequest request
    ) {
        SchoolPlan schoolPlan = schoolPlanService.save(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(schoolPlan.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> update(
            @PathVariable("id") UUID schoolPlanId,
            @RequestBody @Valid UpdateSchoolPlanRequest request
    ) {
        schoolPlanService.update(schoolPlanId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> delete(
            @PathVariable("id") UUID schoolPlanId
    ) {
        schoolPlanService.delete(schoolPlanId);
        return ResponseEntity.noContent().build();
    }
}
