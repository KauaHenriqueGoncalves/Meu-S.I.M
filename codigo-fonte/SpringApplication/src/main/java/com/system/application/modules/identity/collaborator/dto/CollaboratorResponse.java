package com.system.application.modules.identity.collaborator.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record CollaboratorResponse(
        UUID id,
        String username,
        String specialty,
        String workload
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
