package com.system.application.auth.dto;

import com.system.application.integration.captcha.dto.CaptchaRequest;
import com.system.application.shared.validation.NoEmoji;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record LoginRequest(
        @NotBlank(message = "O código da escola é obrigatório")
        @Size(max = 50, message = "O código da escola deve ter no máximo 50 caracteres")
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String schoolCode,

        @NotBlank(message = "O e-mail é obrigatório")
        @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres")
        @Email(message = "Informe um e-mail válido")
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 20, message = "A senha deve ter entre 8 e 20 caracteres")
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String password,

        @Valid
        @NotNull(message = "O captcha é obrigatório")
        CaptchaRequest captchaRequest
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public LoginRequest {
        email = email.toLowerCase();
    }
}
