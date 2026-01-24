package com.system.application.domain.collaborator.dto;

import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public record UpdateCollaboratorRequest(
        @NotBlank(message = "Username can't be blank")
        @Size(max = 100, message = "Username must be lower then 100")
        @NoLeadingTrailingSpace
        String username,

        @NotBlank(message = "Email can't be blank")
        @Email(message = "Email format incorrect")
        @Size(max = 255, message = "Email is up to 255")
        String email,

        @NotBlank(message = "PhoneNumber can't be blank")
        @Size(max = 20, message = "PhoneNumber is up to 255")
        @NoLeadingTrailingSpace
        String phoneNumber,

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser no passado")
        LocalDate dateOfBirth,

        @NotNull(message = "Address can't be null")
        @Size(max = 100, message = "Address must be lower then 100")
        @NoLeadingTrailingSpace
        String address,

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