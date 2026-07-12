package com.system.application.modules.school.service;

import com.system.application.modules.academic.student.repository.StudentRepository;
import com.system.application.modules.identity.collaborator.repository.CollaboratorRepository;
import com.system.application.modules.identity.legalguardian.repository.LegalGuardianRepository;
import com.system.application.modules.identity.schooladmin.repository.SchoolAdminRepository;
import com.system.application.modules.school.dto.SchoolCapacityResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SchoolCapacityQueryImpl implements SchoolCapacityQuery {
    private final Logger log =
            LoggerFactory.getLogger(SchoolCapacityQueryImpl.class);

    private final StudentRepository studentRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final LegalGuardianRepository legalGuardianRepository;
    private final SchoolAdminRepository schoolAdminRepository;

    public SchoolCapacityQueryImpl(
            StudentRepository studentRepository,
            CollaboratorRepository collaboratorRepository,
            LegalGuardianRepository legalGuardianRepository,
            SchoolAdminRepository schoolAdminRepository
    ) {
        this.studentRepository = studentRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.legalGuardianRepository = legalGuardianRepository;
        this.schoolAdminRepository = schoolAdminRepository;
    }

    @Override
    public SchoolCapacityResponseDTO getCapacity(UUID schoolId) {
        if (schoolId == null) {
            log.error("schoolId is null. [schoolId={}]", schoolId);
            throw new IllegalArgumentException("schoolId cannot be null");
        }

        return new SchoolCapacityResponseDTO(
                studentRepository.countBySchoolId(schoolId),
                collaboratorRepository.countBySchoolId(schoolId),
                legalGuardianRepository.countBySchoolId(schoolId),
                schoolAdminRepository.countBySchoolId(schoolId)
        );
    }
}
