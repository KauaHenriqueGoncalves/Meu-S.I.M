package com.system.application.domain.school.service;

import com.system.application.domain.school.School;
import com.system.application.domain.school.dto.SchoolRequest;
import com.system.application.domain.school.repository.SchoolRepository;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SchoolServiceImpl implements SchoolService {
    private final SchoolRepository schoolRepository;

    public SchoolServiceImpl(
            SchoolRepository schoolRepository
    ) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    public School findById(UUID schoolId) {
        return schoolRepository.findById(schoolId)
                .orElseThrow(() -> new NotFoundObjectException("Escola não encontrada"));
    }

    @Override
    public School findByUserId(UUID userId) {
        return schoolRepository.findSchoolByUserId(userId)
                .orElseThrow(() -> new NotFoundObjectException("Escola não encontrada"));
    }

    @Override
    @Transactional
    public School save(SchoolRequest request) {
        boolean existConflict = schoolRepository.existsConflict(request.nameCode(), request.cnpj());
        if (existConflict) throw new EntityAlreadyExistsException("Escola já existente");
        School school = new School(
                null,
                request.nameCode(),
                request.schoolName(),
                request.cnpj()
        );
        school = schoolRepository.save(school);
        return school;
    }
}
