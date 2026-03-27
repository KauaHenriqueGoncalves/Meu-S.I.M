package com.system.application.integration.payment.gateway.dto;

public record CheckoutResponse(

        String preferenceId,
        String initPoint

) { }