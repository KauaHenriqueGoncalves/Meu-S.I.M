package com.system.application.domain.user.controller;

import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test-school-admin-authorize")
    @PreAuthorize("hasAnyAuthority('SCOPE_school_admin', 'SCOPE_system_admin')")
    public ResponseEntity<User> testSchoolAdmin(JwtAuthenticationToken jwtToken) {
        return ResponseEntity.ok(userService.findById(UUID.fromString(jwtToken.getName())));
    }

    @GetMapping("/test-system-admin-authorize")
    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    public ResponseEntity<String> testSystemAdmin() {
        return ResponseEntity.ok("System Admin access ok!");
    }

    @GetMapping("/test-colaborator-authorize")
    @PreAuthorize("hasAnyAuthority('SCOPE_collaborator', 'SCOPE_school_admin', 'SCOPE_legal_guardian')")
    public ResponseEntity<User> testCollaborator(JwtAuthenticationToken jwtToken) {
        return ResponseEntity.ok(userService.findById(UUID.fromString(jwtToken.getName())));
    }
}
