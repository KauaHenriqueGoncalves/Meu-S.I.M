package com.system.application.domain.user.controller;

import com.system.application.domain.user.mapper.UserMapper;
import com.system.application.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> testSchoolAdmin() {
        return ResponseEntity.ok("School Admin access ok!");
    }

    @PreAuthorize("hasAuthority('SCOPE_system_admin')")
    @GetMapping("/test-system-admin-authorize")
    public ResponseEntity<String> testSystemAdmin() {
        return ResponseEntity.ok("System Admin access ok!");
    }
}
