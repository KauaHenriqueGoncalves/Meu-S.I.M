package com.system.application.integration.payment.mercadopago.webhook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MercadoPagoNotification(
        @NotBlank(message = "Action is required")
        String action,

        @JsonProperty("api_version")
        String apiVersion,

        @NotNull(message = "Data is required")
        @Valid
        DataDto data,

        @NotNull(message = "Data is required")
        @JsonProperty("date_created")
        String dateCreated,

        Long id,

        @JsonProperty("live_mode")
        Boolean liveMode,

        String type,

        @JsonProperty("user_id")
        Long userId
) {
    public record DataDto(
            @NotBlank(message = "Data ID is required")
            String id
    ) {}
}