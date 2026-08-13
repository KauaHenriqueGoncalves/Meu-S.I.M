package com.system.application.modules.identity.systemadmin.service;

import com.system.application.modules.identity.systemadmin.SystemAdmin;
import com.system.application.modules.identity.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import java.util.List;
import java.util.UUID;

public interface SystemAdminService {
    List<SystemAdmin> findAll();
    List<SystemAdmin> findAllWithCache();
    SystemAdmin findById(UUID id);
    SystemAdmin findByIdWithCache(UUID id);
    SystemAdmin findByCpfAndEmail(String cpf, String email);
    SystemAdmin save(UserRequest request);
    void update(UUID id, UpdateSystemAdminRequestDTO update);
    void updatePassword(UUID id, PasswordRequest request);
    void deleteById(UUID id);
}
