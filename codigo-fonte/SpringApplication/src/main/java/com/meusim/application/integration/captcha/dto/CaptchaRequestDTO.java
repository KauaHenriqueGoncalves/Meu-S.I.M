package com.meusim.application.integration.captcha.dto;

import jakarta.validation.constraints.NotBlank;

public record CaptchaRequestDTO(
        @NotBlank(message = "Token de verificação obrigatório")
        String token
) { }
