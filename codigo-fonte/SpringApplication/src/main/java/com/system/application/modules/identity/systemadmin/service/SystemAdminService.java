package com.system.application.modules.identity.systemadmin.service;

import com.system.application.modules.identity.systemadmin.SystemAdmin;
import com.system.application.modules.identity.user.dto.UserRequest;

public interface SystemAdminService {
    SystemAdmin findByCpfAndEmail(String cpf, String email);
    SystemAdmin save(UserRequest request);
}
