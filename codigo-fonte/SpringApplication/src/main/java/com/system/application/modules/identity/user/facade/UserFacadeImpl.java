package com.system.application.modules.identity.user.facade;

import com.system.application.modules.identity.role.Role;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.dto.MeResponseDTO;
import com.system.application.modules.identity.user.dto.UserRequest;
import com.system.application.modules.identity.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserFacadeImpl implements UserFacade {
    private final UserService service;

    public UserFacadeImpl(UserService service) {
        this.service = service;
    }

    @Override
    public User getByIdEntity(UUID id) {
        return service.findById(id);
    }

    @Override
    public User getUserForLoginEntity(String email, String schoolCode) {
        return service.findUserForLogin(email, schoolCode);
    }

    @Override
    public User createUserRole(UserRequest request, Role.Values role) {
        return service.registerUserWithRole(request, role);
    }

    @Override
    public MeResponseDTO me() {
        User user = service.findByOwnerWithCache();
        return MeResponseDTO.of(user);
    }

    @Override
    public void activeUserById(UUID id) {
        service.activateUser(id);
    }
}
