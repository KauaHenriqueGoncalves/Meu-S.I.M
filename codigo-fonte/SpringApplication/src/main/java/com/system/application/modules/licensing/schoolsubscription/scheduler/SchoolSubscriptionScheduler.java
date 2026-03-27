package com.system.application.modules.licensing.schoolsubscription.scheduler;

import com.system.application.modules.licensing.schoolpayment.SchoolPayment;
import com.system.application.modules.licensing.schoolpayment.enums.PaymentStatus;
import com.system.application.modules.licensing.schoolpayment.service.SchoolPaymentService;
import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.licensing.schoolsubscription.enums.SubscriptionStatus;
import com.system.application.modules.licensing.schoolsubscription.repository.SchoolSubscriptionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Component
public class SchoolSubscriptionScheduler {
    private static final Logger log =
            LoggerFactory.getLogger(SchoolSubscriptionScheduler.class);

    private final SchoolSubscriptionRepository schoolSubscriptionRepository;
    private final SchoolPaymentService schoolPaymentService;

    public SchoolSubscriptionScheduler(
            SchoolSubscriptionRepository schoolSubscriptionRepository,
            SchoolPaymentService schoolPaymentService
    ) {
        this.schoolSubscriptionRepository = schoolSubscriptionRepository;
        this.schoolPaymentService = schoolPaymentService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void expireSubscriptions() {
        log.info("Iniciando job de expiracao de assinaturas. [data={}]", LocalDate.now());

        List<SchoolSubscription> expired = schoolSubscriptionRepository
                .findAllByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, LocalDate.now());

        if (expired.isEmpty()) {
            log.info("Nenhuma assinatura expirada encontrada.");
            return;
        }

        expired.forEach(sub -> {
            log.info("Expirando assinatura. [schoolSubscriptionId={}] [schoolId={}] [endDate={}]",
                    sub.getId(), sub.getSchool().getId(), sub.getEndDate());
            sub.setStatus(SubscriptionStatus.EXPIRED);
        });

        schoolSubscriptionRepository.saveAll(expired);

        log.info("Job de expiracao de assinaturas concluido. [total={}]", expired.size());
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void expirePendingSubscriptions() {
        log.info("Iniciando job de expiracao de pagamentos pendentes. [instant={}]", Instant.now());

        List<SchoolPayment> expiredPayments = schoolPaymentService
                .findAllByStatusAndExpiresAtBefore(PaymentStatus.PENDING, Instant.now());

        if (expiredPayments.isEmpty()) {
            log.info("Nenhum pagamento pendente expirado encontrado.");
            return;
        }

        log.info("Pagamentos pendentes expirados encontrados. [total={}]", expiredPayments.size());

        expiredPayments.forEach(payment -> {
            log.info("Expirando pagamento pendente. [paymentId={}] [schoolSubscriptionId={}]",
                    payment.getId(), payment.getSchoolSubscription().getId());

            payment.setStatus(PaymentStatus.EXPIRED);
            schoolPaymentService.save(payment);

            schoolSubscriptionRepository.findById(payment.getSchoolSubscription().getId())
                    .ifPresentOrElse(
                            sub -> {
                                sub.setStatus(SubscriptionStatus.EXPIRED);
                                schoolSubscriptionRepository.save(sub);
                                log.info("Assinatura expirada por pagamento nao confirmado. [schoolSubscriptionId={}] [paymentId={}]",
                                        sub.getId(), payment.getId());
                                },
                            () -> log.warn("Assinatura nao encontrada ao expirar pagamento. [paymentId={}] [schoolSubscriptionId={}]",
                                    payment.getId(), payment.getSchoolSubscription().getId())
                    );
        });

        log.info("Job de expiracao de pagamentos pendentes concluido. [total={}]", expiredPayments.size());
    }
}
