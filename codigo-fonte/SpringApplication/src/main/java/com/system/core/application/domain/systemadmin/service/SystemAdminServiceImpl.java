package com.system.core.application.domain.systemadmin.service;

import com.system.core.application.domain.role.Role;
import com.system.core.application.domain.systemadmin.SystemAdmin;
import com.system.core.application.domain.systemadmin.repository.SystemAdminRepository;
import com.system.core.application.domain.user.User;
import com.system.core.application.domain.user.dto.UserRequest;
import com.system.core.application.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class SystemAdminServiceImpl implements SystemAdminService {
    private final SystemAdminRepository systemAdminRepository;
    private final UserService userService;

    public SystemAdminServiceImpl(
            SystemAdminRepository systemAdminRepository,
            UserService userService
    ) {
        this.systemAdminRepository = systemAdminRepository;
        this.userService = userService;
    }

    @Override
    public SystemAdmin findByCpfAndEmail(String cpf, String email) {
        return systemAdminRepository.findByUserCpfAndUserEmail(cpf, email)
                .orElseThrow(() -> new BadCredentialsException("Credenciais incorretas"));
    }

    @Override
    @Transactional
    public SystemAdmin save(UserRequest request) {
        User user = userService.registerUserWithRole(request, Role.Values.SYSTEM_ADMIN);
        SystemAdmin systemAdmin = new SystemAdmin(null, user);
        systemAdmin = systemAdminRepository.save(systemAdmin);
        return systemAdmin;
    }
}
