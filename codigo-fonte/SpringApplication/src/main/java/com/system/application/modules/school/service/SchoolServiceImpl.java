package com.system.application.modules.school.service;

import com.system.application.modules.school.School;
import com.system.application.modules.school.dto.SchoolRequest;
import com.system.application.modules.school.repository.SchoolRepository;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SchoolServiceImpl implements SchoolService {
    private static final Logger log =
            LoggerFactory.getLogger(SchoolServiceImpl.class);

    private final SchoolRepository schoolRepository;

    public SchoolServiceImpl(
            SchoolRepository schoolRepository
    ) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    public School findById(UUID schoolId) {
        return schoolRepository.findById(schoolId)
                .orElseThrow(
                        () -> new NotFoundObjectException("Escola não encontrada")
                );
    }

    @Override
    public School findByUserId(UUID userId) {
        return schoolRepository.findSchoolByUserId(userId)
                .orElseThrow(
                        () -> new NotFoundObjectException("Escola não encontrada")
                );
    }

    @Override
    @Transactional
    public School save(SchoolRequest request) {
        checkSchoolConflict(request);
        School school = new School(
                null,
                request.nameCode(),
                request.schoolName(),
                request.cnpj()
        );
        school = schoolRepository.save(school);
        return school;
    }

    private void checkSchoolConflict(SchoolRequest request) {
        if (schoolRepository.existsByNameCode(request.nameCode())) {
            log.warn("Tentativa de cadastro com Codigo do reforco ja cadastrado. [nameCode={}]", request.nameCode());
            throw new EntityAlreadyExistsException("Código do reforço já cadastrado");
        }
        if (schoolRepository.existsByCnpj(request.cnpj())) {
            log.warn("Tentativa de cadastro com CNPJ já cadastrado. [cnpj={}]", request.cnpj());
            throw new EntityAlreadyExistsException("Cnpj já cadastrado");
        }
    }
}
