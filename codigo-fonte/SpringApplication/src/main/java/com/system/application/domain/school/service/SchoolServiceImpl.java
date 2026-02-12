package com.system.application.domain.school.service;

import com.system.application.domain.school.School;
import com.system.application.domain.school.repository.SchoolRepository;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SchoolServiceImpl implements SchoolService {
    private final SchoolRepository schoolRepository;

    public SchoolServiceImpl(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    public School findById(UUID id) {
        return schoolRepository.findById(id).orElseThrow(
                () -> new NotFoundObjectException("Not found School")
        );
    }

    @Override
    @Transactional
    public School save(School school) {
        Boolean existConflict = schoolRepository.existsConflict(school.getNameCode(), school.getCnpj());
        if (existConflict) throw new EntityAlreadyExistsException("School already exists");
        return schoolRepository.save(school);
    }
}
