package com.system.core.application.domain.systemadmin.service;

import com.system.core.application.domain.systemadmin.SystemAdmin;
import com.system.core.application.domain.user.dto.UserRequest;

public interface SystemAdminService {
    SystemAdmin findByCpfAndEmail(String cpf, String email);
    SystemAdmin save(UserRequest request);
}
