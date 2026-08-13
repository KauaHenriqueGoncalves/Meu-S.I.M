package com.system.application.modules.identity.systemadmin.facade;

import com.system.application.modules.identity.systemadmin.SystemAdmin;
import com.system.application.modules.identity.systemadmin.dto.SystemAdminDetailViewResponseDTO;
import com.system.application.modules.identity.systemadmin.dto.SystemAdminSimpleViewResponseDTO;
import com.system.application.modules.identity.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.identity.user.dto.UserRequest;

import java.util.List;
import java.util.UUID;

public interface SystemAdminFacade {
    List<SystemAdmin> getAllEntities();
    List<SystemAdminSimpleViewResponseDTO> getAll();
    SystemAdmin getByIdEntity(UUID id);
    SystemAdminDetailViewResponseDTO getById(UUID id);
    void create(UserRequest request);
    void update(UUID id, UpdateSystemAdminRequestDTO updateRequest);
    void updatePassword(UUID id, PasswordRequest request);
    void delete(UUID id);
}
