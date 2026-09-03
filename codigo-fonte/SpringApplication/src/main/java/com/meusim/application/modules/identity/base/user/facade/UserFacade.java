package com.meusim.application.modules.identity.base.user.facade;

import com.meusim.application.modules.identity.base.role.Role;
import com.meusim.application.modules.identity.base.user.User;
import com.meusim.application.modules.identity.base.user.dto.MeResponseDTO;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.meusim.application.modules.identity.base.user.dto.ResponsibleSnapshotDTO;
import java.util.UUID;

public interface UserFacade {
    User getByIdEntity(UUID id);
    User getUserForLoginEntity(String email, String schoolCode);
    User createUserRole(CreateUserRequestDTO request, Role.Values role);
    ResponsibleSnapshotDTO getResponsible(UUID userId);
    MeResponseDTO me();
    void activeUserById(UUID id);
}
