package com.system.application.auth.dto;

import com.system.application.shared.validation.NoEmoji;
import com.system.application.shared.validation.ValidCpf;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record AdminLoginRequest(
        @NotNull(message = "cpf can't be null")
        @NotBlank(message = "cpf can't be blank")
        @Size(min = 11, max = 11, message = "CPF must have 11 characters.")
        @ValidCpf(message = "Cpf must be valid!")
        String cpf,

        @NotBlank
        @NotNull
        @Size(max = 255)
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String email,

        @NotBlank
        @NotNull
        @Size(min = 8, max = 20)
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String password
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public AdminLoginRequest {
        email = email.toLowerCase();
    }
}
