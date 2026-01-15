package com.system.application.domain.school.service;

import com.system.application.domain.school.School;
import com.system.application.domain.school.repository.SchoolRepository;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import org.springframework.stereotype.Service;

@Service
public final class SchoolService {
    private final SchoolRepository schoolRepository;

    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public School save(School school) {
        Boolean existConflict = schoolRepository.existsConflict(school.getNameCode(), school.getCnpj());
        if (existConflict) throw new EntityAlreadyExistsException("School already exists");
        return schoolRepository.save(school);
    }
}
