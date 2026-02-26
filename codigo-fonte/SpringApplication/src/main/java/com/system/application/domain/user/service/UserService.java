package com.system.application.domain.user.service;

import com.system.application.domain.role.Role;
import com.system.application.domain.user.User;
import com.system.application.domain.user.dto.UserRequest;

import java.util.UUID;

public interface UserService {
    User findById(UUID id);
    User findUserForLogin(String email, String schoolCode);
    User registerUserWithRole(UserRequest request, Role.Values role);
    void activateUser(UUID id);
}
