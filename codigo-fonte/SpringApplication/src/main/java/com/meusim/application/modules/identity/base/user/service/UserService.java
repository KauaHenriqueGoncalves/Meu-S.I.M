package com.meusim.application.modules.identity.base.user.service;

import com.meusim.application.modules.identity.base.role.Role;
import com.meusim.application.modules.identity.base.user.User;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import java.util.UUID;

public interface UserService {
    User findById(UUID id);
    User findByOwnerWithCache();
    User findUserForLogin(String email, String schoolCode);
    User registerUserWithRole(CreateUserRequestDTO dto, Role.Values role);
    void activateUser(UUID id);
}
