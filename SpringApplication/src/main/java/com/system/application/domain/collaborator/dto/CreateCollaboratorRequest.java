package com.system.application.domain.collaborator.dto;

import com.system.application.domain.user.dto.UserRequest;
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
