package com.system.application.domain.schooladmin.controller;

import com.system.application.domain.schooladmin.SchoolAdmin;
import com.system.application.domain.schooladmin.dto.CreateSchoolAdminRequest;
import com.system.application.domain.schooladmin.service.SchoolAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public final class SchoolAdminController {
    private final SchoolAdminService schoolAdminService;

    public SchoolAdminController(
            SchoolAdminService schoolAdminService
    ) {
        this.schoolAdminService = schoolAdminService;
    }

    @PostMapping("/school-admin")
    public ResponseEntity<Void> create(
            @RequestBody @Valid CreateSchoolAdminRequest request
    ) {
        SchoolAdmin schoolAdmin = schoolAdminService.save(request.userRequest(), request.schoolRequest());
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(schoolAdmin.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }
}
