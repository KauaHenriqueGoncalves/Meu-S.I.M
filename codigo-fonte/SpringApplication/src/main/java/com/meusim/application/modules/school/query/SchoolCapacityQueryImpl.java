package com.meusim.application.modules.school.query;

import com.meusim.application.modules.academic.student.repository.StudentRepository;
import com.meusim.application.modules.identity.profile.collaborator.repository.CollaboratorRepository;
import com.meusim.application.modules.identity.profile.legalguardian.repository.LegalGuardianRepository;
import com.meusim.application.modules.identity.profile.schooladmin.repository.SchoolAdminRepository;
import com.meusim.application.modules.school.dto.SchoolCapacityResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SchoolCapacityQueryImpl implements SchoolCapacityQuery {
    private final Logger log = LoggerFactory.getLogger(SchoolCapacityQueryImpl.class);
    private final StudentRepository studentRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final LegalGuardianRepository legalGuardianRepository;
    private final SchoolAdminRepository schoolAdminRepository;

    public SchoolCapacityQueryImpl(
            StudentRepository studentRepository,
            CollaboratorRepository collaboratorRepository,
            LegalGuardianRepository legalGuardianRepository,
            SchoolAdminRepository schoolAdminRepository) {
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
