package com.meusim.application.modules.identity.base.user.facade;

import com.meusim.application.modules.identity.base.role.Role;
import com.meusim.application.modules.identity.base.user.User;
import com.meusim.application.modules.identity.base.user.dto.CurrentUserDTO;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.meusim.application.modules.identity.base.user.dto.ResponsibleSnapshotDTO;
import com.meusim.application.modules.identity.base.user.query.CurrentUserQuery;
import com.meusim.application.modules.identity.base.user.query.ResponsibleSnapshotQuery;
import com.meusim.application.modules.identity.base.user.service.UserService;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UserFacadeImpl implements UserFacade {
    private final UserService userService;
    private final ResponsibleSnapshotQuery responsibleSnapshotQuery;
    private final CurrentUserQuery currentUserQuery;

    public UserFacadeImpl(
            UserService userService,
            ResponsibleSnapshotQuery responsibleSnapshotQuery,
            CurrentUserQuery currentUserQuery
    ) {
        this.userService = userService;
        this.responsibleSnapshotQuery = responsibleSnapshotQuery;
        this.currentUserQuery = currentUserQuery;
    }

    @Override
    public User getByIdEntity(UUID id) {
        return userService.findById(id);
    }

    @Override
    public User getUserForLoginEntity(String email, String schoolCode) {
        return userService.findUserForLogin(email, schoolCode);
    }

    @Override
    public User createUserRole(CreateUserRequestDTO request, Role.Values role) {
        return userService.registerUserWithRole(request, role);
    }

    @Override
    public ResponsibleSnapshotDTO getResponsible(UUID userId) {
        return responsibleSnapshotQuery.findResponsibleSnapshotByUserId(userId);
    }

    @Override
    public CurrentUserDTO me() {
        return currentUserQuery.get();
    }

    @Override
    public void activeUserById(UUID id) {
        userService.activateUser(id);
    }
}
