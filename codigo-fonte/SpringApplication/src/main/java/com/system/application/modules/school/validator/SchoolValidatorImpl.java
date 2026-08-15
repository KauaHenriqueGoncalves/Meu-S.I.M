package com.system.application.modules.school.validator;

import com.system.application.modules.school.School;
import com.system.application.modules.school.repository.SchoolRepository;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SchoolValidatorImpl implements SchoolValidator {
    private static final Logger log = LoggerFactory.getLogger(SchoolValidatorImpl.class);
    private final SchoolRepository schoolRepository;

    public SchoolValidatorImpl(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
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
}
