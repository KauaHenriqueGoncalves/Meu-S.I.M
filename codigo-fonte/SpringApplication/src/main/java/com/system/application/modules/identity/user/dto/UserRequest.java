package com.system.application.modules.identity.user.dto;

import com.system.application.shared.validation.NoEmoji;
import com.system.application.shared.validation.NoLeadingTrailingSpace;
import com.system.application.shared.validation.ValidCpf;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record UserRequest(
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

        @NotBlank(message = "Senha não pode ser vazio")
        @Size(min = 8, max = 20, message = "Senha deve ser entre 8 e 20 caracteres")
        @NoLeadingTrailingSpace
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String password,

        @NotBlank(message = "Cpf não pode ser vazio")
        @Size(min = 11, max = 11, message = "Cpf deve ter 11 caracteres")
        @ValidCpf(message = "Cpf deve ser válido")
        String cpf,

        @NotBlank(message = "Número de telefone não pode ser vazio")
        @Size(max = 20, message = "Número de telefone deve ser menor que 20 caracteres")
        @NoLeadingTrailingSpace
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String phoneNumber,

        @NotNull(message = "Endereço não pode ser nulo")
        @Size(max = 100, message = "Endereço deve ser menor que 100 caracteres")
        @NoLeadingTrailingSpace
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String address
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserRequest {
        email = email.toLowerCase();
    }
}
