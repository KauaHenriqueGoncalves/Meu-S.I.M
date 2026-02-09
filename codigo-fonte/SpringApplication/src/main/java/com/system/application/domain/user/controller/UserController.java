package com.system.application.domain.user.controller;

import com.system.application.domain.user.User;
import com.system.application.domain.user.mapper.UserMapper;
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
    private final UserMapper userMapper;

    public UserController(UserService userService,
                          UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_school_admin', 'SCOPE_system_admin')")
    @GetMapping("/test-school-admin-authorize")
    public ResponseEntity<User> testSchoolAdmin(JwtAuthenticationToken jwtToken) {
        return ResponseEntity.ok(userService.findById(UUID.fromString(jwtToken.getName())));
    }

    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    @GetMapping("/test-system-admin-authorize")
    public ResponseEntity<String> testSystemAdmin() {
        return ResponseEntity.ok("System Admin access ok!");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_collaborator', 'SCOPE_school_admin', 'SCOPE_legal_guardian')")
    @GetMapping("/test-colaborator-authorize")
    public ResponseEntity<User> testCollaborator(JwtAuthenticationToken jwtToken) {
        return ResponseEntity.ok(userService.findById(UUID.fromString(jwtToken.getName())));
    }
}
