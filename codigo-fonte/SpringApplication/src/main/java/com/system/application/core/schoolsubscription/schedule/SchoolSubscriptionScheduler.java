package com.system.application.core.schoolsubscription.schedule;

import com.system.application.core.schoolsubscription.SchoolSubscription;
import com.system.application.core.schoolsubscription.enums.SchoolSubscriptionStatus;
import com.system.application.core.schoolsubscription.repository.SchoolSubscriptionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class SchoolSubscriptionScheduler {
    private final SchoolSubscriptionRepository schoolSubscriptionRepository;

    public SchoolSubscriptionScheduler(
            SchoolSubscriptionRepository schoolSubscriptionRepository
    ) {
        this.schoolSubscriptionRepository = schoolSubscriptionRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void expireSubscriptions() {
        List<SchoolSubscription> expired = schoolSubscriptionRepository
                .findAllByStatusAndEndDateBefore(SchoolSubscriptionStatus.ACTIVE, LocalDate.now());
        expired.forEach(sub ->
                sub.setStatus(SchoolSubscriptionStatus.CANCELED));
        schoolSubscriptionRepository.saveAll(expired);
    }
}
