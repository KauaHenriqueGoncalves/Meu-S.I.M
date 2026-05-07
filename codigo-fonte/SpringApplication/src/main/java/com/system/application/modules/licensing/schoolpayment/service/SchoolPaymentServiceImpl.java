package com.system.application.modules.licensing.schoolpayment.service;

import com.system.application.modules.licensing.schoolpayment.SchoolPayment;
import com.system.application.modules.licensing.schoolpayment.dto.SchoolPaymentRequest;
import com.system.application.modules.licensing.schoolpayment.enums.PaymentStatus;
import com.system.application.modules.licensing.schoolpayment.repository.SchoolPaymentRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SchoolPaymentServiceImpl implements SchoolPaymentService {
    private static final Logger log =
            LoggerFactory.getLogger(SchoolPaymentServiceImpl.class);

    private final SchoolPaymentRepository schoolPaymentRepository;

    public SchoolPaymentServiceImpl(
            SchoolPaymentRepository schoolPaymentRepository
    ) {
        this.schoolPaymentRepository = schoolPaymentRepository;
    }

    @Override
    public SchoolPayment findBySchoolSubscriptionId(UUID schoolSubscriptionId) {
        return schoolPaymentRepository.findBySchoolSubscriptionId(schoolSubscriptionId)
                .orElseThrow(() -> {
                    log.warn("Pagamento da escola nao encontrado pela assinatura. [schoolSubscriptionId={}]",
                            schoolSubscriptionId);
                    return new NotFoundObjectException("Historico de pagamento nao encontrado");
                });
    }

    @Override
    public List<SchoolPayment> findAllByStatusAndExpiresAtBefore(PaymentStatus status, Instant expiresAt) {
        log.info("Buscando pagamentos expirados. [status={}] [expiresAt={}]",
                status, expiresAt);

        List<SchoolPayment> payments =
                schoolPaymentRepository.findExpiredPendingPayments(status, expiresAt);

        log.info("Pagamentos expirados encontrados. [status={}] [total={}]",
                status, payments.size());

        return payments;
    }

    @Override
    public SchoolPayment findById(UUID paymentId) {
        return schoolPaymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.warn("Pagamento da escola nao encontrado. [paymentId={}]", paymentId);
                    return new NotFoundObjectException("Historico de pagamento nao encontrado");
                });
    }

    @Override
    @Transactional
    public SchoolPayment save(SchoolPayment schoolPayment) {
        log.info("Salvando pagamento da escola. [paymentId={}] [schoolSubscriptionId={}] [status={}]",
                schoolPayment.getId(), schoolPayment.getSchoolSubscription().getId(), schoolPayment.getStatus());

        SchoolPayment saved = schoolPaymentRepository.save(schoolPayment);

        log.info("Pagamento da escola salvo com sucesso. [paymentId={}] [status={}]",
                saved.getId(), saved.getStatus());

        return saved;
    }

    @Override
    @Transactional
    public SchoolPayment create(SchoolPaymentRequest request) {
        log.info("Iniciando criacao de pagamento da escola. [schoolSubscriptionId={}] [valor={}] [providerId={}]",
                request.schoolSubscription().getId(), request.amount(), request.providerPaymentId());

        SchoolPayment payment = SchoolPayment.createInit(request);
        payment = schoolPaymentRepository.save(payment);

        log.info("Pagamento da escola criado com sucesso. [paymentId={}] [schoolSubscriptionId={}] [valor={}]",
                payment.getId(), request.schoolSubscription().getId(), request.amount());

        return payment;
    }
}
