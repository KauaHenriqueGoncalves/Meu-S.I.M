package com.system.core.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record LoginRequest(
        @NotBlank
        @NotNull
        @Size(max = 50)
        String schoolCode,

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

    public LoginRequest {
        email = email.toLowerCase();
    }
}
