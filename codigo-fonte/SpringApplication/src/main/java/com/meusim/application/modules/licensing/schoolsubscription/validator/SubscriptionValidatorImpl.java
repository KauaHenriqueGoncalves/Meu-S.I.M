package com.meusim.application.modules.licensing.schoolsubscription.validator;

import com.meusim.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.meusim.application.modules.licensing.schoolsubscription.enums.SubscriptionStatus;
import com.meusim.application.modules.licensing.schoolsubscription.repository.SchoolSubscriptionRepository;
import com.meusim.application.modules.school.School;
import com.meusim.application.shared.exception.BusinessException;
import com.meusim.application.shared.exception.SubscriptionException;
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

    @Override
    public void ensureSubscriptionSupportsSchoolAdminCount(School school, int current) {
        log.info("Verificando se o reforco possui uma licenca ativa e suporta a quantidade de admins. [schoolId={}] [current={}]",
                school.getId(), current);
        SchoolSubscription sub = subscriptionRepository
                .findBySchoolIdAndStatus(school.getId(), SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("OPERACAO BLOQUEADA: o reforco não possui uma licenca ativa. [schoolId={}] [current={}]",
                            school.getId(), current);
                    return new SubscriptionException("A escola não possui uma licença ativa.");
                });
        if (current >= sub.getMaxSchoolAdmin()) {
            log.warn("A licenca do reforco não suporta adicionar mais administradores. [schoolId={}] [current={}] [subscriptionId={}] [maxSchoolAdmin={}]",
                    school.getId(), current, sub.getId(), sub.getMaxSchoolAdmin());
            throw new BusinessException("A licença não suporta o número de administradores");
        }
    }
}
