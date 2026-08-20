package com.system.application.modules.identity.profile.schooladmin.validator;

import com.system.application.modules.identity.profile.schooladmin.repository.SchoolAdminRepository;
import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.licensing.schoolsubscription.validator.SubscriptionValidator;
import com.system.application.modules.school.School;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.BusinessException;
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
    public void ensureSubscriptionSupportsSchoolAdminCound(School school, SchoolSubscription sub) {
        int current = repository.countBySchoolId(school.getId());
        if (current >= sub.getMaxSchoolAdmin()) {
            log.warn("A licenca do reforco não suporta adicionar mais administradores. [schoolId={}] [current={}] [subscriptionId={}] [maxSchoolAdmin={}]",
                    school.getId(), current, sub.getId(), sub.getMaxSchoolAdmin());
            throw new BusinessException("A licença não suporta o número de administradores");
        }
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
