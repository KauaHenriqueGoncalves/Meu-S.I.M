package com.system.application.domain.legalGuardian.service;

import com.system.application.domain.legalGuardian.LegalGuardian;
import com.system.application.domain.legalGuardian.dto.LegalGuardianRequest;
import com.system.application.domain.legalGuardian.repository.LegalGuardianRepository;
import com.system.application.domain.school.School;
import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.schoolAdmin.service.SchoolAdminService;
import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import com.system.application.shared.exception.AccessDeniedException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LegalGuardianServiceImpl implements LegalGuardianService {
    private final UserService userService;
    private final SchoolAdminService schoolAdminService;
    private final LegalGuardianRepository legalGuardianRepository;

    public LegalGuardianServiceImpl(UserService userService,
                                    SchoolAdminService schoolAdminService,
                                    LegalGuardianRepository legalGuardianRepository) {
        this.userService = userService;
        this.schoolAdminService = schoolAdminService;
        this.legalGuardianRepository = legalGuardianRepository;
    }

    @Override
    @Transactional
    public UUID saveLegalGuardian(User user, UUID adminId, LegalGuardianRequest legalGuardianRequest) {
        user = userService.saveLegalGuardian(user);
        SchoolAdmin schoolAdmin = schoolAdminService.findByUserId(adminId);
        School school = schoolAdmin.getSchoolId();
        LegalGuardian legalGuardian = new LegalGuardian(
                null,
                user,
                school,
                legalGuardianRequest.degreeOfKinship()
        );
        legalGuardian = legalGuardianRepository.save(legalGuardian);
        return legalGuardian.getId();
    }
}
