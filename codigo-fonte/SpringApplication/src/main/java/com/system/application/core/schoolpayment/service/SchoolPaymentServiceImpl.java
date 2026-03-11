package com.system.application.core.schoolpayment.service;

import com.system.application.core.schoolpayment.SchoolPayment;
import com.system.application.core.schoolpayment.dto.SchoolPaymentRequest;
import com.system.application.core.schoolpayment.enums.PaymentStatus;
import com.system.application.core.schoolpayment.repository.SchoolPaymentRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
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
    public List<SchoolPayment> findAllByStatusAndExpiresAtBefore(PaymentStatus status, Instant expiresAt) {
        return schoolPaymentRepository
                .findExpiredPendingPayments(status, expiresAt);
    }

    @Override
    public SchoolPayment findById(UUID paymentId) {
        return schoolPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundObjectException("Historico de pagamento não encontrado"));
    }

    @Override
    @Transactional
    public SchoolPayment save(SchoolPayment schoolPayment) {
        return schoolPaymentRepository.save(schoolPayment);
    }

    @Override
    @Transactional
    public SchoolPayment create(SchoolPaymentRequest request) {
        SchoolPayment payment = SchoolPayment.createInit(request);
        return schoolPaymentRepository.save(payment);
    }
}
