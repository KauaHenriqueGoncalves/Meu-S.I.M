package com.system.application.modules.identity.collaborator.dto;

import com.system.application.modules.identity.user.dto.UserRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record CreateCollaboratorRequest(

        @Valid @NotNull UserRequest userRequest,
        @Valid @NotNull CollaboratorRequest collaboratorRequest

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
