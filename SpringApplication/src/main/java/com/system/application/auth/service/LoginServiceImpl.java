package com.system.application.auth.service;

import com.system.application.auth.dto.AdminLoginRequest;
import com.system.application.auth.dto.LoginRequest;
import com.system.application.auth.dto.LoginResponse;
import com.system.application.domain.role.Role;
import com.system.application.domain.systemAdmin.SystemAdmin;
import com.system.application.domain.systemAdmin.service.SystemAdminService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import com.system.application.shared.exception.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelo processo de autenticação de usuários do sistema.
 *
 * <p>
 * Este serviço centraliza as regras de login para diferentes tipos de usuários,
 * garantindo validações de credenciais, status da conta e permissões.
 * </p>
 *
 * Regras gerais:
 * <ul>
 *   <li>A conta do usuário deve estar ativa</li>
 *   <li>A senha deve ser validada utilizando {@link PasswordEncoder}</li>
 *   <li>Em caso de falha, exceções de segurança são lançadas</li>
 * </ul>
 */
@Service
public final class LoginServiceImpl implements LoginService {
    private final UserService userService;
    private final SystemAdminService systemAdminService;
    private final PasswordEncoder passwordEncoder;

    public LoginServiceImpl(UserService userService,
                            SystemAdminService systemAdminService,
                            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.systemAdminService = systemAdminService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Realiza o login de usuários vinculados a uma instituição.
     *
     * <p>
     * Fluxo de autenticação:
     * <ol>
     *   <li>Busca o usuário pelo email e código da escola</li>
     *   <li>Verifica se a conta está ativa</li>
     *   <li>Valida a senha informada</li>
     * </ol>
     * </p>
     *
     * @param loginRequest dados de autenticação do usuário
     * @return resposta contendo o id do usuário e suas roles
     * @throws AccessDeniedException se a conta estiver inativa
     * @throws BadCredentialsException se as credenciais forem inválidas
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.findForLogin(loginRequest.email(), loginRequest.schoolCode());
        if (!user.getActive()) {
            throw new AccessDeniedException("A conta não está ativa!");
        }
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return new LoginResponse(user.getId(), user.getRole());
    }

    /**
     * Realiza o login de administradores do sistema (System Admin).
     *
     * <p>
     * Este metodo valida se o usuário possui a role
     * {@link Role.Values#SYSTEM_ADMIN} antes de permitir a autenticação.
     * </p>
     *
     * Regras específicas:
     * <ul>
     *   <li>O usuário deve possuir a role SYSTEM_ADMIN</li>
     *   <li>A conta deve estar ativa</li>
     *   <li>A senha deve ser válida</li>
     * </ul>
     *
     * @param adminLoginRequest dados de autenticação do administrador do sistema
     * @return resposta contendo o id do usuário e suas roles
     * @throws BadCredentialsException se o usuário não possuir a role adequada ou a senha for inválida
     * @throws AccessDeniedException se a conta estiver inativa
     */
    @Override
    public LoginResponse login(AdminLoginRequest adminLoginRequest) {
        SystemAdmin systemAdmin = systemAdminService
                .findByUserCpfAndUserEmail(adminLoginRequest.cpf(), adminLoginRequest.email());
        User user = systemAdmin.getUser();
        boolean isSystemAdmin = user.getRole().stream()
                .anyMatch(role -> role.getId() == Role.Values.SYSTEM_ADMIN.getValue());
        if (!isSystemAdmin) {
            throw new BadCredentialsException("Invalid credentials");
        }
        if (!user.getActive()) {
            throw new AccessDeniedException("A conta não está ativa!");
        }
        if (!passwordEncoder.matches(adminLoginRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return new LoginResponse(user.getId(), user.getRole());
    }
}
