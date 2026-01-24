package com.system.application.domain.legalGuardian.dto;

import com.system.application.domain.user.dto.UserRequest;
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
