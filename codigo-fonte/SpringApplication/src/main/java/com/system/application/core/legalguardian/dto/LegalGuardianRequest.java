package com.system.application.core.legalguardian.dto;

import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record LegalGuardianRequest(
        @NotBlank(message = "Nível de Parentesco não pode ser vazio")
        @Size(min = 3, max = 30, message = "Nível de Parentesco deve ter entre 3 e 30 caracteres")
        @NoLeadingTrailingSpace
        String degreeOfKinship
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
