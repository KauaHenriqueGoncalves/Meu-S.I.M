package com.system.application.integration.payment.mercadopago.dto;

import java.time.OffsetDateTime;

public record MercadoPagoPaymentResult(
        String id,
        String orderId,
        String externalReference,  // school_subscription_id
        String status,
        String statusDetail,
        int installments,
        String paymentMethod,      // ex: "master", "visa", "pix"
        String paymentType,        // ex: "credit_card", "pix", "debit_card"
        OffsetDateTime paidAt
) { }
