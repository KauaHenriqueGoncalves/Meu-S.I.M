package com.system.application.modules.licensing.schoolsubscription.dto;

import java.time.OffsetDateTime;

public record PaymentResult(
        String paymentMethodId,  // ex: "visa", "master", "pix"
        String paymentTypeId,    // ex: "credit_card", "debit_card", "pix"
        Integer installments,
        String orderId,
        OffsetDateTime paidAt
) {}