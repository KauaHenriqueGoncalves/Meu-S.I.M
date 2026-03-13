package com.system.application.modules.licensing.schoolpayment.service;

import com.system.application.modules.licensing.schoolpayment.SchoolPayment;
import com.system.application.modules.licensing.schoolpayment.dto.SchoolPaymentRequest;
import com.system.application.modules.licensing.schoolpayment.enums.PaymentStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SchoolPaymentService {
    SchoolPayment findBySchoolSubscriptionId(UUID schoolSubscriptionId);
    List<SchoolPayment> findAllByStatusAndExpiresAtBefore(PaymentStatus status, Instant expiresAt);
    SchoolPayment findById(UUID paymentId);
    SchoolPayment save(SchoolPayment schoolPayment);
    SchoolPayment create(SchoolPaymentRequest request);
}
