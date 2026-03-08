package com.system.application.core.schoolpayment.service;

import com.system.application.core.schoolpayment.SchoolPayment;
import com.system.application.core.schoolpayment.dto.SchoolPaymentRequest;

import java.util.UUID;

public interface SchoolPaymentService {
    SchoolPayment findBySchoolSubscriptionId(UUID schoolSubscriptionId);
    SchoolPayment create(SchoolPaymentRequest request);
}
