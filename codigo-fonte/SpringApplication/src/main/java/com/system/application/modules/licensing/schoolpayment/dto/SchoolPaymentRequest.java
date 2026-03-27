package com.system.application.modules.licensing.schoolpayment.dto;

import com.system.application.modules.licensing.schoolpayment.enums.PaymentStatus;
import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;

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
