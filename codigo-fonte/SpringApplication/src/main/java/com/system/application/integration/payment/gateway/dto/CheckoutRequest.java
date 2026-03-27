package com.system.application.integration.payment.gateway.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CheckoutRequest(

        UUID referenceId,        // subscriptionId
        String title,
        String description,
        BigDecimal amount,
        Integer installments,
        PayerInfo payer

) { }