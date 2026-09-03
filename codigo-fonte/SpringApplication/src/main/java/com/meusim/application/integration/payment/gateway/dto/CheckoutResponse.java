package com.meusim.application.integration.payment.gateway.dto;

public record CheckoutResponse(

        String preferenceId,
        String initPoint

) { }