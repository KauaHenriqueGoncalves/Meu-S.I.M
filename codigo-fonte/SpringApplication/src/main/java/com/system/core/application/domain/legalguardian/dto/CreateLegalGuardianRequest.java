package com.system.core.application.domain.legalguardian.dto;

import com.system.core.application.domain.user.dto.UserRequest;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;

public record CreateLegalGuardianRequest(
        @Valid UserRequest userRequest,
        @Valid LegalGuardianRequest legalGuardianRequest
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
