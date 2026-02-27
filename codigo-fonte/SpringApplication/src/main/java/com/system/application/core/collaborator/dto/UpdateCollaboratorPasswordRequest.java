package com.system.application.core.collaborator.dto;

import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record UpdateCollaboratorPasswordRequest(
        @NotBlank(message = "Senha não pode ser vazio")
        @Size(min = 8, max = 20, message = "Senha deve ser entre 8 e 20 caracteres")
        @NoLeadingTrailingSpace
        String newPassword
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
