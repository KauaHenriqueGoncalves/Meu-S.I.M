package com.system.application.integration.captcha.dto;

import jakarta.validation.constraints.NotBlank;

public record CaptchaRequest(
        @NotBlank(message = "Token de verificação obrigatório")
        String captchaToken
) { }
