package com.system.application.core.systemadmin.service;

import com.system.application.core.systemadmin.SystemAdmin;
import com.system.application.core.user.dto.UserRequest;

public interface SystemAdminService {
    SystemAdmin findByCpfAndEmail(String cpf, String email);
    SystemAdmin save(UserRequest request);
}
