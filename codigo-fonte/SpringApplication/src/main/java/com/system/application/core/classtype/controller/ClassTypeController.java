package com.system.application.core.classtype.controller;

import com.system.application.core.classtype.ClassType;
import com.system.application.core.classtype.service.ClassTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/class-type")
public class ClassTypeController {
    private final ClassTypeService classTypeService;

    public ClassTypeController(
            ClassTypeService classTypeService
    ) {
        this.classTypeService = classTypeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_school_admin', 'SCOPE_collaborator', 'SCOPE_legal_guardian')")
    public ResponseEntity<Set<ClassType>> findAll() {
        Set<ClassType> types = classTypeService.findAll();
        return ResponseEntity.ok(types);
    }
}
