package com.meusim.application.modules.identity.profile.collaborator.dto;

import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record CreateCollaboratorRequest(
        @Valid @NotNull CreateUserRequestDTO createUserRequestDTO,
        @Valid @NotNull CollaboratorRequest collaboratorRequest
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
