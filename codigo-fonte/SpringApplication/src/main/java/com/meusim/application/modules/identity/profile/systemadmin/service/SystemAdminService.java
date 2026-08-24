package com.meusim.application.modules.identity.profile.systemadmin.service;

import com.meusim.application.modules.identity.profile.systemadmin.SystemAdmin;
import com.meusim.application.modules.identity.profile.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.meusim.application.modules.identity.base.user.dto.PasswordRequest;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
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
