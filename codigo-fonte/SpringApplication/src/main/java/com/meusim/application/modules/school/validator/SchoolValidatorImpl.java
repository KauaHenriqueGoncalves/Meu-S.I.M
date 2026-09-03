package com.meusim.application.modules.school.validator;

import com.meusim.application.modules.licensing.schoolsubscription.validator.SubscriptionValidator;
import com.meusim.application.modules.school.School;
import com.meusim.application.modules.school.repository.SchoolRepository;
import com.meusim.application.shared.exception.AccessDeniedException;
import com.meusim.application.shared.exception.EntityAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SchoolValidatorImpl implements SchoolValidator {
    private static final Logger log = LoggerFactory.getLogger(SchoolValidatorImpl.class);
    private final SchoolRepository schoolRepository;
    private final SubscriptionValidator subscriptionValidator;

    public SchoolValidatorImpl(SchoolRepository schoolRepository,
                               SubscriptionValidator subscriptionValidator) {
        this.schoolRepository = schoolRepository;
        this.subscriptionValidator = subscriptionValidator;
    }

    @Override
    public void ensureSchoolSameByOwnerId(School userSchool, School ownerSchool) {
        if (!userSchool.getId().equals(ownerSchool.getId())) {
            log.warn("Administrador e usuario nao pertencem ao mesmo reforco. [userSchoolId={}] [ownerSchoolId={}]",
                    userSchool.getId(), ownerSchool.getId());
            throw new AccessDeniedException("Administrador e Usuario nao pertencem ao mesmo reforco");
        }
    }

    @Override
    public void ensureSchoolSameByOwnerId(UUID userSchoolId, School ownerSchool) {
        if (!ownerSchool.getId().equals(userSchoolId)) {
            log.warn("Administrador e usuario nao pertencem ao mesmo reforco. [userSchoolId={}] [ownerSchoolId={}]",
                    userSchoolId, ownerSchool.getId());
            throw new AccessDeniedException("Administrador e Usuario nao pertencem ao mesmo reforco");
        }
    }

    @Override
    public void ensureSchoolAlreadyExistNameCode(String nameCode) {
        if (schoolRepository.existsByNameCode(nameCode)) {
            log.warn("Tentativa de cadastro com Codigo do reforco ja cadastrado. [nameCode={}]", nameCode);
            throw new EntityAlreadyExistsException("Código do reforço já cadastrado");
        }
    }

    @Override
    public void ensureSchoolAlreadyExistCnpj(String cnpj) {
        boolean cnpjIsNull = cnpj == null;
        if (!cnpjIsNull) {
            if (schoolRepository.existsByCnpj(cnpj)) {
                log.warn("Tentativa de cadastro com CNPJ ja cadastrado. [cnpj={}]", cnpj);
                throw new EntityAlreadyExistsException("CNPJ já cadastrado");
            }
        }
    }

    @Override
    public void ensureSchoolHasSubscription(School school) {
        subscriptionValidator.ensureSchoolHasActiveSubscription(school);
    }
}
