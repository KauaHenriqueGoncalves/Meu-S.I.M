package com.system.application.core.collaborator.dto;

import com.system.application.core.user.dto.UserRequest;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;

public record CreateCollaboratorRequest(
        @Valid UserRequest userRequest,
        @Valid CollaboratorRequest collaboratorRequest
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
