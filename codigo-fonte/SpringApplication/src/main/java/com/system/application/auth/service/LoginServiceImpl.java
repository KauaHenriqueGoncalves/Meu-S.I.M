package com.system.application.auth.service;

import com.system.application.auth.dto.AdminLoginRequest;
import com.system.application.auth.dto.LoginRequest;
import com.system.application.auth.dto.LoginResponse;
import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.systemadmin.SystemAdmin;
import com.system.application.modules.identity.systemadmin.service.SystemAdminService;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.service.UserService;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.SubscriptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public final class LoginServiceImpl implements LoginService {
    private static final Logger log =
            LoggerFactory.getLogger(LoginServiceImpl.class);

    private final UserService userService;
    private final SchoolSubscriptionService schoolSubscriptionService;
    private final SystemAdminService systemAdminService;
    private final PasswordEncoder passwordEncoder;

    public LoginServiceImpl(
            UserService userService,
            SchoolSubscriptionService schoolSubscriptionService,
            SystemAdminService systemAdminService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.schoolSubscriptionService = schoolSubscriptionService;
        this.systemAdminService = systemAdminService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Tentativa de login. [email={}] [schoolCode={}]",
                loginRequest.email(), loginRequest.schoolCode());

        User user = userService.findUserForLogin(loginRequest.email(), loginRequest.schoolCode());

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            log.warn("Senha incorreta na tentativa de login. [userId={}] [email={}]",
                    user.getId(), user.getEmail());
            throw new BadCredentialsException("Credenciais incorretas");
        }

        if (!user.getActive()) {
            log.warn("Tentativa de login em conta inativa. [userId={}] [email={}]",
                    user.getId(), user.getEmail());
            throw new AccessDeniedException("A conta não está ativa!");
        }

        boolean isCollaboratorOrLegalGuardian = user.getRole().stream()
                .anyMatch(role -> (role.getId() == Role.Values.LEGAL_GUARDIAN.getValue()) ||
                        (role.getId() == Role.Values.COLLABORATOR.getValue()));

        if (isCollaboratorOrLegalGuardian) {
            try {
                schoolSubscriptionService.findActiveSubscription(user.getId());
            }
            catch (SubscriptionException e) {
                log.warn("Login bloqueado: escola sem licenca ativa. [userId={}] [email={}]",
                        user.getId(), user.getEmail());
                throw new SubscriptionException("A escola precisa ter licenca ativa para que responsaveis e colaboradores possam acessar o sistema");
            }
        }

        log.info("Login realizado com sucesso. [userId={}] [email={}]",
                user.getId(), user.getEmail());

        return new LoginResponse(user.getId(), user.getRole());
    }

    @Override
    public LoginResponse login(AdminLoginRequest adminLoginRequest) {
        log.info("Tentativa de login de administrador do sistema. [email={}] [cpf={}]",
                adminLoginRequest.email(), adminLoginRequest.cpf());

        SystemAdmin systemAdmin = systemAdminService
                .findByCpfAndEmail(adminLoginRequest.cpf(), adminLoginRequest.email());
        User user = systemAdmin.getUser();

        if (!passwordEncoder.matches(adminLoginRequest.password(), user.getPassword())) {
            log.warn("Senha incorreta na tentativa de login do administrador. [userId={}] [email={}]",
                    user.getId(), user.getEmail());
            throw new BadCredentialsException("Credenciais incorretas");
        }

        if (!user.getActive()) {
            log.warn("Tentativa de login em conta de administrador inativa. [userId={}] [email={}]",
                    user.getId(), user.getEmail());
            throw new AccessDeniedException("A conta não está ativa!");
        }

        boolean isSystemAdmin = user.getRole().stream()
                .anyMatch(role -> role.getId() == Role.Values.SYSTEM_ADMIN.getValue());
        if (!isSystemAdmin) {
            log.warn("Usuario sem role de administrador tentou acessar login administrativo. [userId={}] [email={}] [roles={}]",
                    user.getId(), user.getEmail(), user.getRole());
            throw new BadCredentialsException("Credenciais incorretas");
        }

        log.info("Login de administrador do sistema realizado com sucesso. [userId={}] [email={}]",
                user.getId(), user.getEmail());

        return new LoginResponse(user.getId(), user.getRole());
    }
}
