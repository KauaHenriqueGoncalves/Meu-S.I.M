package com.system.application.modules.identity.base.user.facade;

import com.system.application.modules.identity.base.role.Role;
import com.system.application.modules.identity.base.user.User;
import com.system.application.modules.identity.base.user.dto.MeResponseDTO;
import com.system.application.modules.identity.base.user.dto.CreateUserRequestDTO;

import java.util.UUID;

public interface UserFacade {
    User getByIdEntity(UUID id);
    User getUserForLoginEntity(String email, String schoolCode);
    User createUserRole(CreateUserRequestDTO request, Role.Values role);
    MeResponseDTO me();
    void activeUserById(UUID id);
}
