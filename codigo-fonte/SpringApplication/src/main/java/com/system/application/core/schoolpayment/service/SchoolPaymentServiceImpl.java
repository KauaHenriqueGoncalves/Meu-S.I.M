package com.system.application.core.schoolpayment.service;

import com.system.application.core.schoolpayment.SchoolPayment;
import com.system.application.core.schoolpayment.dto.SchoolPaymentRequest;
import com.system.application.core.schoolpayment.repository.SchoolPaymentRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SchoolPaymentServiceImpl implements SchoolPaymentService {
    private final SchoolPaymentRepository schoolPaymentRepository;

    public SchoolPaymentServiceImpl(
            SchoolPaymentRepository schoolPaymentRepository
    ) {
        this.schoolPaymentRepository = schoolPaymentRepository;
    }


    @Override
    public SchoolPayment findBySchoolSubscriptionId(UUID schoolSubscriptionId) {
        return schoolPaymentRepository.findBySchoolSubscriptionId(schoolSubscriptionId)
                .orElseThrow(() -> new NotFoundObjectException("Historico de pagamento não encontrado"));
    }

    @Override
    @Transactional
    public SchoolPayment create(SchoolPaymentRequest request) {
        SchoolPayment payment = new SchoolPayment(
                null,
                request.schoolSubscription(),
                request.discountAmount(),
                request.originalAmount(),
                request.amount(),
                request.paymentMethod(),
                request.status(),
                null
        );
        payment = schoolPaymentRepository.save(payment);
        return payment;
    }
}
