package com.system.application.modules.identity.user.controller;

import com.system.application.modules.identity.user.dto.MeResponseDTO;
import com.system.application.modules.identity.user.facade.UserFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserFacade facade;

    public UserController(UserFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('SCOPE_system_admin', 'SCOPE_school_admin', 'SCOPE_collaborator', 'SCOPE_legal_guardian')")
    public ResponseEntity<MeResponseDTO> me() {
        return ResponseEntity.ok(facade.me());
    }
}
