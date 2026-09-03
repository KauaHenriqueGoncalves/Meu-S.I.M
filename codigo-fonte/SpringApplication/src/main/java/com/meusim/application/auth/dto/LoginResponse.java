package com.meusim.application.auth.dto;

import com.meusim.application.modules.identity.base.role.Role;

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
