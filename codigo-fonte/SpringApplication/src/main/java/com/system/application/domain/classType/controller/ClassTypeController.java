package com.system.application.domain.classType.controller;

import com.system.application.domain.classType.ClassType;
import com.system.application.domain.classType.service.ClassTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/class-type")
public class ClassTypeController {
    private final ClassTypeService classTypeService;

    public ClassTypeController(ClassTypeService classTypeService) {
        this.classTypeService = classTypeService;
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_school_admin', 'SCOPE_collaborator', 'SCOPE_legal_guardian')")
    @GetMapping
    public ResponseEntity<Set<ClassType>> findAll(JwtAuthenticationToken token) {
        Set<ClassType> types = classTypeService.findAll();
        return ResponseEntity.ok(types);
    }
}
