package com.meusim.application.modules.identity.profile.legalguardian.dto;

import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record CreateLegalGuardianRequest(
        @Valid @NotNull CreateUserRequestDTO createUserRequestDTO,
        @Valid @NotNull LegalGuardianRequest legalGuardianRequest
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
