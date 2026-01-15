package com.system.application.domain.user.dto;

import com.system.application.shared.exception.CpfInvalidException;
import com.system.application.shared.util.CpfValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record UserRequest(
        @NotBlank(message = "Username can't be blank")
        @NotNull(message = "Username can't be null")
        @Size(max = 100, message = "Username must be lower then 100")
        String username,

        @NotNull(message = "Email can't be null")
        @NotBlank(message = "Email can't be blank")
        @Email(message = "Email format incorrect")
        @Size(max = 255, message = "Email is up to 255")
        String email,

        @NotNull(message = "Password can't be null")
        @NotBlank(message = "Password can't be blank")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20")
        String password,

        @NotNull(message = "cpf can't be null")
        @NotBlank(message = "cpf can't be blank")
        @Size(min = 11, max = 11, message = "CPF must have 11 characters.")
        String cpf,

        @NotNull(message = "phonenumber can't be null")
        @NotBlank(message = "phoneNumber can't be blank")
        @Size(max = 20, message = "phoneNumber must be lower 20 characters.")
        String phoneNumber
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public UserRequest {
        if (!CpfValidator.getInstance().isValid(cpf)) {
            throw new CpfInvalidException("Cpf must be valid!");
        }
    }
}
