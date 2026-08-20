package com.system.application.modules.licensing.schoolsubscription.validator;

import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.licensing.schoolsubscription.enums.SubscriptionStatus;
import com.system.application.modules.licensing.schoolsubscription.repository.SchoolSubscriptionRepository;
import com.system.application.modules.school.School;
import com.system.application.shared.exception.SubscriptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionValidatorImpl implements SubscriptionValidator {
    private static final Logger log = LoggerFactory.getLogger(SubscriptionValidatorImpl.class);
    private final SchoolSubscriptionRepository subscriptionRepository;

    public SubscriptionValidatorImpl(SchoolSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public void ensureSchoolHasActiveSubscription(School school) {
        log.info("Verificando se o reforco possui uma licenca ativa. [schoolId={}]", school.getId());
        SchoolSubscription sub = subscriptionRepository
                .findBySchoolIdAndStatus(school.getId(), SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("OPERACAO BLOQUEADA: o reforco não possui uma licenca ativa. [schoolId={}]", school.getId());
                    return new SubscriptionException("O reforço não possui uma licença ativa.");
                });
    }
}
