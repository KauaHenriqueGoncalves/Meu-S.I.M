package com.system.application.modules.identity.profile.systemadmin.controller;

import com.system.application.modules.identity.profile.systemadmin.dto.SystemAdminDetailViewResponseDTO;
import com.system.application.modules.identity.profile.systemadmin.dto.SystemAdminSimpleViewResponseDTO;
import com.system.application.modules.identity.profile.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.system.application.modules.identity.profile.systemadmin.facade.SystemAdminFacade;
import com.system.application.modules.identity.base.user.dto.PasswordRequest;
import com.system.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/system-admin")
public class SystemAdminController {
    private final SystemAdminFacade facade;

    public SystemAdminController(SystemAdminFacade facade) {
        this.facade = facade;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<List<SystemAdminSimpleViewResponseDTO>> findAll() {
        return ResponseEntity.ok(facade.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<SystemAdminDetailViewResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(facade.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> create(@RequestBody @Valid CreateUserRequestDTO request) {
        facade.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateSystemAdminRequestDTO request) {
        facade.update(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> updatePassword(@PathVariable UUID id, @RequestBody @Valid PasswordRequest request){
        facade.updatePassword(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        facade.delete(id);
        return ResponseEntity.noContent().build();
    }
}
