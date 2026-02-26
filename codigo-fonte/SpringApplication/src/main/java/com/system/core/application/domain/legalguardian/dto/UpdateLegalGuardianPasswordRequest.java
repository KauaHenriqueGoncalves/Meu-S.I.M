package com.system.core.application.domain.legalguardian.dto;

import com.system.core.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record UpdateLegalGuardianPasswordRequest(
        @NotBlank(message = "Password can't be blank")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20")
        @NoLeadingTrailingSpace
        String newPassword
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
