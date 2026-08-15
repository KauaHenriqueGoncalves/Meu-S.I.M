package com.system.application.modules.school.controller;

import com.system.application.modules.school.dto.SchoolResponseDTO;
import com.system.application.modules.school.dto.UpdateSchoolRequestDTO;
import com.system.application.modules.school.facade.SchoolFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/school")
public class SchoolController {
    private final SchoolFacade facade;

    public SchoolController(SchoolFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('SCOPE_school_admin', 'SCOPE_collaborator', 'SCOPE_legal_guardian')")
    public ResponseEntity<SchoolResponseDTO> findMySchool() {
        return ResponseEntity.ok(facade.getByOwnerId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody UpdateSchoolRequestDTO dto) {
        facade.update(id, dto);
        return ResponseEntity.ok().build();
    }
}
