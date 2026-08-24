package com.meusim.application.modules.identity.base.user.facade;

import com.meusim.application.modules.identity.base.role.Role;
import com.meusim.application.modules.identity.base.user.User;
import com.meusim.application.modules.identity.base.user.dto.MeResponseDTO;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.meusim.application.modules.identity.base.user.service.UserService;
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
    public User createUserRole(CreateUserRequestDTO request, Role.Values role) {
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
