package com.system.application.integration.payment.mercadopago.event;

public record PaymentNotificationEvent(
        Long resourceId,
        String resourceType
) { }
