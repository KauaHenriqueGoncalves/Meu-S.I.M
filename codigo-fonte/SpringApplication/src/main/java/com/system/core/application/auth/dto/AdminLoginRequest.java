package com.system.core.application.auth.dto;

import com.system.core.application.shared.exception.CpfInvalidException;
import com.system.core.application.shared.util.CpfValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record AdminLoginRequest(
        @NotNull(message = "cpf can't be null")
        @NotBlank(message = "cpf can't be blank")
        @Size(min = 11, max = 11, message = "CPF must have 11 characters.")
        String cpf,

        @NotBlank
        @NotNull
        @Size(max = 255)
        String email,

        @NotBlank
        @NotNull
        @Size(min = 8, max = 20)
        String password
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public AdminLoginRequest {
        if (!CpfValidator.getInstance().isValid(cpf)) {
            throw new CpfInvalidException("Cpf must be valid!");
        }
        email = email.toLowerCase();
    }
}
