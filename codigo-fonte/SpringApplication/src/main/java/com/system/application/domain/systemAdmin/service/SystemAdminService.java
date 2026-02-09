package com.system.application.domain.systemAdmin.service;

import com.system.application.domain.systemAdmin.SystemAdmin;
import com.system.application.domain.user.User;

import java.util.UUID;

public interface SystemAdminService {
    UUID saveSystemAdmin(User user);
    SystemAdmin findByUserCpfAndUserEmail(String cpf, String email);
}
