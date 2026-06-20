package com.system.application.modules.identity.user.dto;

import com.system.application.shared.validation.NoEmoji;
import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record PasswordRequest(
        @NotBlank(message = "Senha não pode ser vazio")
        @Size(min = 8, max = 20, message = "Senha deve ser entre 8 e 20 caracteres")
        @NoLeadingTrailingSpace
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String newPassword
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
