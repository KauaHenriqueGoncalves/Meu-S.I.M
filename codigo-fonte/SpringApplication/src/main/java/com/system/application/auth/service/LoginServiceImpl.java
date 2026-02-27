package com.system.application.auth.service;

import com.system.application.auth.dto.AdminLoginRequest;
import com.system.application.auth.dto.LoginRequest;
import com.system.application.auth.dto.LoginResponse;
import com.system.application.core.role.Role;
import com.system.application.core.systemadmin.SystemAdmin;
import com.system.application.core.systemadmin.service.SystemAdminService;
import com.system.application.core.user.User;
import com.system.application.core.user.service.UserService;
import com.system.application.shared.exception.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public final class LoginServiceImpl implements LoginService {
    private final UserService userService;
    private final SystemAdminService systemAdminService;
    private final PasswordEncoder passwordEncoder;

    public LoginServiceImpl(
            UserService userService,
            SystemAdminService systemAdminService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.systemAdminService = systemAdminService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.findUserForLogin(loginRequest.email(), loginRequest.schoolCode());
        if (!user.getActive()) {
            throw new AccessDeniedException("A conta não está ativa!");
        }
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Credenciais incorretas");
        }
        return new LoginResponse(user.getId(), user.getRole());
    }

    @Override
    public LoginResponse login(AdminLoginRequest adminLoginRequest) {
        SystemAdmin systemAdmin = systemAdminService
                .findByCpfAndEmail(adminLoginRequest.cpf(), adminLoginRequest.email());
        User user = systemAdmin.getUser();
        boolean isSystemAdmin = user.getRole().stream()
                .anyMatch(role -> role.getId() == Role.Values.SYSTEM_ADMIN.getValue());
        if (!isSystemAdmin) {
            throw new BadCredentialsException("Credenciais incorretas");
        }
        if (!user.getActive()) {
            throw new AccessDeniedException("A conta não está ativa!");
        }
        if (!passwordEncoder.matches(adminLoginRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Credenciais incorretas");
        }
        return new LoginResponse(user.getId(), user.getRole());
    }
}
