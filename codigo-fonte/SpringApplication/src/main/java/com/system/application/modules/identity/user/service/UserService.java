package com.system.application.modules.identity.user.service;

import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.dto.UserRequest;

import java.util.UUID;

public interface UserService {
    User findById(UUID id);
    User findUserForLogin(String email, String schoolCode);
    User registerUserWithRole(UserRequest request, Role.Values role);
    void activateUser(UUID id);
}
