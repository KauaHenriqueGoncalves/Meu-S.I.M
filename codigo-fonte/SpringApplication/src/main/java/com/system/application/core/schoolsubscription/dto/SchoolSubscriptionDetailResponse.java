package com.system.application.core.schoolsubscription.dto;

import com.system.application.core.schoolpayment.SchoolPayment;
import com.system.application.core.schoolpayment.enums.PaymentMethod;
import com.system.application.core.schoolpayment.enums.PaymentStatus;
import com.system.application.core.schoolsubscription.SchoolSubscription;
import com.system.application.core.schoolsubscription.enums.SubscriptionStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SchoolSubscriptionDetailResponse(
        UUID id,
        Integer months,
        String planName,
        BigDecimal planPrice,
        Integer maxStudents,
        Integer maxCollaborators,
        Integer maxLegalGuardian,
        Integer maxSchoolAdmin,
        LocalDate startDate,
        LocalDate endDate,
        SubscriptionStatus subscriptionStatus,
        BigDecimal discountAmount,
        BigDecimal originalAmount,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer installments,
        String paymentType,
        Instant paidAt,
        PaymentStatus paymentStatus,
        String providerPaymentId
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static SchoolSubscriptionDetailResponse from(
            SchoolSubscription subscription,
            SchoolPayment payment
    ) {
        return new SchoolSubscriptionDetailResponse(
                subscription.getId(),
                subscription.getMonths(),
                subscription.getPlanName(),
                subscription.getPlanPrice(),
                subscription.getMaxStudents(),
                subscription.getMaxCollaborators(),
                subscription.getMaxLegalGuardian(),
                subscription.getMaxSchoolAdmin(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                subscription.getStatus(),
                payment.getDiscountAmount(),
                payment.getOriginalAmount(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getInstallments(),
                payment.getPaymentType(),
                payment.getPaidAt(),
                payment.getStatus(),
                payment.getProviderPaymentId()
        );
    }
}
