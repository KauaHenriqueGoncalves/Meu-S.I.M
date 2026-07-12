package com.system.application.modules.identity.legalguardian.dto;

import com.system.application.modules.identity.user.dto.UserRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public record CreateLegalGuardianRequest(
        @Valid @NotNull UserRequest userRequest,
        @Valid @NotNull LegalGuardianRequest legalGuardianRequest
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
