package com.meusim.application.modules.school.controller;

import com.meusim.application.modules.school.dto.SchoolResponseDTO;
import com.meusim.application.modules.school.dto.UpdateSchoolRequestDTO;
import com.meusim.application.modules.school.facade.SchoolFacade;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/schools")
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
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid  UpdateSchoolRequestDTO dto) {
        facade.update(id, dto);
        return ResponseEntity.ok().build();
    }
}
