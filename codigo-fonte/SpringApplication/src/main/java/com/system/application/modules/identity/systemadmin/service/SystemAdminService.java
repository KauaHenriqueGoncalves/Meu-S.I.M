package com.system.application.modules.identity.systemadmin.service;

import com.system.application.modules.identity.systemadmin.SystemAdmin;
import com.system.application.modules.identity.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import java.util.List;
import java.util.UUID;

public interface SystemAdminService {
    List<SystemAdmin> findAll(UUID userId);
    SystemAdmin findById(UUID userId, UUID id);
    SystemAdmin findByIdWithCache(UUID userId, UUID id);
    SystemAdmin findByCpfAndEmail(String cpf, String email);
    SystemAdmin save(UUID userId, UserRequest request);
    void update(UUID userId, UUID id, UpdateSystemAdminRequestDTO update);
    void updatePassword(UUID userId, UUID id, PasswordRequest request);
    void deleteById(UUID userId, UUID id);
}
