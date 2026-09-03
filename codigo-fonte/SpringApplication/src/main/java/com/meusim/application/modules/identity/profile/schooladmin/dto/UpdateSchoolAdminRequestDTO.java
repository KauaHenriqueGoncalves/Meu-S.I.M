package com.meusim.application.modules.identity.profile.schooladmin.dto;

import com.meusim.application.shared.validation.NoEmoji;
import com.meusim.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateSchoolAdminRequestDTO(
        @NotBlank(message = "Nome não pode ser vazio")
        @Size(max = 100, message = "Nome deve ser menor que 100 caracteres")
        @NoLeadingTrailingSpace
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String username,

        @NotBlank(message = "Email não pode ser vazio")
        @Email(message = "Formato do Email incorreto")
        @Size(max = 255, message = "Email deve ser menor que 255 caracteres")
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String email,

        @NotBlank(message = "Número de telefone não pode ser vazio")
        @Size(max = 20, message = "Número de telefone deve ser menor que 20 caracteres")
        @NoLeadingTrailingSpace
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String phoneNumber,

        @NotNull(message = "Endereço não pode ser nulo")
        @Size(max = 100, message = "Endereço deve ser menor que 100 caracteres")
        @NoLeadingTrailingSpace
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String address,

        @NotNull(message = "Campo de status deve ser informado")
        Boolean isActive
) { }
