package com.system.core.application.domain.collaborator.dto;

import com.system.core.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public record CollaboratorRequest(
        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser no passado")
        LocalDate dateOfBirth,

        @NotBlank(message = "Especialidade é obrigatória")
        @Size(min = 3, max = 30, message = "Especialidade deve ter entre 3 e 30 caracteres")
        @NoLeadingTrailingSpace
        String specialty,

        @NotBlank(message = "Carga horária é obrigatória")
        @Pattern(regexp = "^(\\d{1,2})h$", message = "Carga horária deve estar no formato '8h', '12h', etc.")
        @NoLeadingTrailingSpace
        String workload
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
