package com.system.application.domain.systemAdmin.service;

import com.system.application.domain.systemAdmin.SystemAdmin;
import com.system.application.domain.systemAdmin.repository.SystemAdminRepository;
import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SystemAdminServiceImpl implements SystemAdminService {
    private final UserService userService;
    private final SystemAdminRepository systemAdminRepository;

    public SystemAdminServiceImpl(UserService userService,
                                  SystemAdminRepository systemAdminRepository) {
        this.userService = userService;
        this.systemAdminRepository = systemAdminRepository;
    }

    @Override
    @Transactional
    public UUID saveSystemAdmin(User user) {
        user = userService.saveSystemAdmin(user);
        SystemAdmin systemAdmin = new SystemAdmin(null, user);
        systemAdmin = systemAdminRepository.save(systemAdmin);
        return systemAdmin.getId();
    }

    @Override
    public SystemAdmin findByUserCpfAndUserEmail(String cpf, String email) {
        return systemAdminRepository.findByUserCpfAndUserEmail(cpf, email).orElseThrow(
                () -> new BadCredentialsException("Invalid credentials")
        );
    }
}
