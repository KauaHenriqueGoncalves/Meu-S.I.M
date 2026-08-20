package com.system.application.modules.identity.base.user.service;

import com.system.application.modules.identity.base.role.Role;
import com.system.application.modules.identity.base.user.User;
import com.system.application.modules.identity.base.user.dto.CreateUserRequestDTO;

import java.util.UUID;

public interface UserService {
    User findById(UUID id);
    User findByOwnerWithCache();
    User findUserForLogin(String email, String schoolCode);
    User registerUserWithRole(CreateUserRequestDTO dto, Role.Values role);
    void activateUser(UUID id);
}
