package com.system.application.domain.collaborator.dto;

import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public record UpdateCollaboratorRequest(
        @NotBlank(message = "Nome não pode ser vazio")
        @Size(max = 100, message = "Nome deve ser menor que 100 caracteres")
        @NoLeadingTrailingSpace
        String username,

        @NotBlank(message = "Email não pode ser vazio")
        @Email(message = "Formato do Email incorreto")
        @Size(max = 255, message = "Email deve ser menor que 255 caracteres")
        String email,

        @NotBlank(message = "Número de telefone não pode ser vazio")
        @Size(max = 20, message = "Número de telefone deve ser menor que 20 caracteres")
        @NoLeadingTrailingSpace
        String phoneNumber,

        @NotNull(message = "Data de nascimento é necessário informar")
        @Past(message = "Data de nascimento deve ser no passado")
        LocalDate dateOfBirth,

        @NotNull(message = "Endereço não pode ser nulo")
        @Size(max = 100, message = "Endereço deve ser menor que 100 caracteres")
        @NoLeadingTrailingSpace
        String address,

        @NotNull(message = "Campo de status deve ser informado")
        Boolean isActive,

        @NotBlank(message = "Especialidade não pode ser vazio")
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