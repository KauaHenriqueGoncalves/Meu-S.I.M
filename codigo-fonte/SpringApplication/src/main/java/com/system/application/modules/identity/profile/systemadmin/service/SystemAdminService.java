package com.system.application.modules.identity.profile.systemadmin.service;

import com.system.application.modules.identity.profile.systemadmin.SystemAdmin;
import com.system.application.modules.identity.profile.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.system.application.modules.identity.base.user.dto.PasswordRequest;
import com.system.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import java.util.List;
import java.util.UUID;

public interface SystemAdminService {
    List<SystemAdmin> findAll();
    List<SystemAdmin> findAllWithCache();
    SystemAdmin findById(UUID id);
    SystemAdmin findByIdWithCache(UUID id);
    SystemAdmin findByCpfAndEmail(String cpf, String email);
    SystemAdmin create(CreateUserRequestDTO dto);
    void update(UUID id, UpdateSystemAdminRequestDTO update);
    void updatePassword(UUID id, PasswordRequest request);
    void deleteById(UUID id);
}
