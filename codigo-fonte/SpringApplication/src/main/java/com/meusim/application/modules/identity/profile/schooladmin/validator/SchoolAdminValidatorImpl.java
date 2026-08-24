package com.meusim.application.modules.identity.profile.schooladmin.validator;

import com.meusim.application.modules.identity.profile.schooladmin.repository.SchoolAdminRepository;
import com.meusim.application.modules.licensing.schoolsubscription.validator.SubscriptionValidator;
import com.meusim.application.modules.school.School;
import com.meusim.application.shared.exception.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SchoolAdminValidatorImpl implements SchoolAdminValidator {
    private static final Logger log = LoggerFactory.getLogger(SchoolAdminValidatorImpl.class);
    private final SchoolAdminRepository repository;
    private final SubscriptionValidator subscriptionValidator;

    public SchoolAdminValidatorImpl(
            SchoolAdminRepository repository,
            SubscriptionValidator subscriptionValidator) {
        this.repository = repository;
        this.subscriptionValidator = subscriptionValidator;
    }

    @Override
    public void ensureSubscriptionSupportsSchoolAdminCount(School school) {
        int current = repository.countBySchoolId(school.getId());
        subscriptionValidator.ensureSubscriptionSupportsSchoolAdminCount(school, current);
    }

    @Override
    public void ensureSchoolAdminAndOwnerBelongsSameSchool(School ownerSchool, School adminSchool) {
        if (!ownerSchool.getId().equals(adminSchool.getId())) {
            log.warn("ALERTA: Tentativa de acesso a administrador do reforco de outra instituição. [ownerSchoolId={}] [adminSchoolId={}]",
                    ownerSchool.getId(), adminSchool.getId());
            throw new AccessDeniedException("Não pode alterar o adminitrador de outra instituição");
        }
    }

    @Override
    public void ensureSchoolHasSubscription(School school) {
        subscriptionValidator.ensureSchoolHasActiveSubscription(school);
    }
}
