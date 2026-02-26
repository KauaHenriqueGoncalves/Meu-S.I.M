package com.system.core.application.auth.dto;

import com.system.core.application.domain.role.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public record LoginResponse(
        UUID id,
        Set<Role> role
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
