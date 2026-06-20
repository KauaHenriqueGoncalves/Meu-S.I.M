package com.system.application.integration.email.dto;

import com.system.application.modules.licensing.schoolsubscription.dto.SchoolSubscriptionDetailResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record SendEmailSubscriptionPaid(
        Integer months,
        String planName,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal amount,
        Integer installments,
        String orderId,
        Instant paidAt,
        String providerPaymentId
) {
    public static SendEmailSubscriptionPaid from(SchoolSubscriptionDetailResponse detail) {
        return new SendEmailSubscriptionPaid(
                detail.months(),
                detail.planName(),
                detail.startDate(),
                detail.endDate(),
                detail.amount(),
                detail.installments(),
                detail.orderId(),
                detail.paidAt(),
                detail.providerPaymentId()
        );
    }
}
