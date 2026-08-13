package com.system.application.modules.identity.user.facade;

import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.dto.MeResponseDTO;
import com.system.application.modules.identity.user.dto.UserRequest;

import java.util.UUID;

public interface UserFacade {
    User getByIdEntity(UUID id);
    User getUserForLoginEntity(String email, String schoolCode);
    User createUserRole(UserRequest request, Role.Values role);
    MeResponseDTO me();
    void activeUserById(UUID id);
}
