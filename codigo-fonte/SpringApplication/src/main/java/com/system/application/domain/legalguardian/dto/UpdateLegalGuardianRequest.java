package com.system.application.domain.legalguardian.dto;

import com.system.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record UpdateLegalGuardianRequest(
        @NotBlank(message = "Nome não pode ser vazi")
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

        @NotNull(message = "Endereço não pode ser nulo")
        @Size(max = 100, message = "Endereço deve ser menor que 100 caracteres")
        @NoLeadingTrailingSpace
        String address,

        @NotNull(message = "Campo de status deve ser informado")
        Boolean isActive,

        @NotBlank(message = "Nível de Parentesco não pode ser vazio")
        @Size(max = 30, message = "Nível de Parentesco deve ter entre 3 e 30 caracteres")
        @NoLeadingTrailingSpace
        String degreeOfKinship
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
