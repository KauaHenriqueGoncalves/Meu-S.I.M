package com.system.application.core.schoolpayment.dto;

import com.system.application.core.schoolpayment.enums.PaymentStatus;
import com.system.application.core.schoolsubscription.SchoolSubscription;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record SchoolPaymentRequest(
        SchoolSubscription schoolSubscription,
        BigDecimal discountAmount,
        BigDecimal originalAmount,
        BigDecimal amount,
        PaymentStatus status,
        String providerPaymentId
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
