package com.system.application.core.schoolsubscription.schedule;

import com.system.application.core.schoolpayment.SchoolPayment;
import com.system.application.core.schoolpayment.enums.PaymentStatus;
import com.system.application.core.schoolpayment.service.SchoolPaymentService;
import com.system.application.core.schoolsubscription.SchoolSubscription;
import com.system.application.core.schoolsubscription.enums.SubscriptionStatus;
import com.system.application.core.schoolsubscription.repository.SchoolSubscriptionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SchoolSubscriptionScheduler {
    private static final Logger log = LoggerFactory.getLogger(SchoolSubscriptionScheduler.class);

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
        List<SchoolSubscription> expired = schoolSubscriptionRepository
                .findAllByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, LocalDate.now());
        expired.forEach(sub ->
                sub.setStatus(SubscriptionStatus.EXPIRED));
        schoolSubscriptionRepository.saveAll(expired);
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void expirePendingSubscriptions() {
        List<SchoolPayment> expiredPayments = schoolPaymentService
                .findAllByStatusAndExpiresAtBefore(
                        PaymentStatus.PENDING,
                        Instant.now()
                );

        if (expiredPayments.isEmpty()) return;

        expiredPayments.forEach(payment -> {
            payment.setStatus(PaymentStatus.EXPIRED);
            schoolPaymentService.save(payment);

            schoolSubscriptionRepository.findById(payment.getSchoolSubscription().getId())
                    .ifPresent(sch -> {
                        sch.setStatus(SubscriptionStatus.EXPIRED);
                        schoolSubscriptionRepository.save(sch);

                        log.info("Subscription {} canceled due to expired payment {}",
                                sch.getId(), payment.getId());
                    });
        });
    }
}
