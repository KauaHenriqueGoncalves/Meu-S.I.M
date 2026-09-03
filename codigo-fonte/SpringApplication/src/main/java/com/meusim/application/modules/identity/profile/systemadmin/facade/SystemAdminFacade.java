package com.meusim.application.modules.identity.profile.systemadmin.facade;

import com.meusim.application.modules.identity.profile.systemadmin.SystemAdmin;
import com.meusim.application.modules.identity.profile.systemadmin.dto.SystemAdminDetailViewResponseDTO;
import com.meusim.application.modules.identity.profile.systemadmin.dto.SystemAdminSimpleViewResponseDTO;
import com.meusim.application.modules.identity.profile.systemadmin.dto.UpdateSystemAdminRequestDTO;
import com.meusim.application.modules.identity.base.user.dto.PasswordRequest;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import java.util.List;
import java.util.UUID;

public interface SystemAdminFacade {
    List<SystemAdmin> getAllEntities();
    List<SystemAdminSimpleViewResponseDTO> getAll();
    SystemAdmin getEntityById(UUID id);
    SystemAdminDetailViewResponseDTO getById(UUID id);
    SystemAdmin getByCpfAndEmailEntity(String cpf, String email);
    void create(CreateUserRequestDTO request);
    void update(UUID id, UpdateSystemAdminRequestDTO updateRequest);
    void updatePassword(UUID id, PasswordRequest request);
    void delete(UUID id);
}
