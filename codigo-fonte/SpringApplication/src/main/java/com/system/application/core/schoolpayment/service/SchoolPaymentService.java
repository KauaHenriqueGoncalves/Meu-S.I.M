package com.system.application.core.schoolpayment.service;

import com.system.application.core.schoolpayment.SchoolPayment;
import com.system.application.core.schoolpayment.dto.SchoolPaymentRequest;
import com.system.application.core.schoolpayment.enums.PaymentStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SchoolPaymentService {
    SchoolPayment findBySchoolSubscriptionId(UUID schoolSubscriptionId);
    List<SchoolPayment> findAllByStatusAndExpiresAtBefore(PaymentStatus status, Instant expiresAt);
    SchoolPayment findById(UUID paymentId);
    SchoolPayment save(SchoolPayment schoolPayment);
    SchoolPayment create(SchoolPaymentRequest request);
}
