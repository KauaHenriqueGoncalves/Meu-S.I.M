package com.system.application.domain.schoolAdmin.service;

import com.system.application.domain.school.School;
import com.system.application.domain.school.service.SchoolService;
import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.schoolAdmin.repository.SchoolAdminRepository;
import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SchoolAdminServiceImpl implements SchoolAdminService {
    private final UserService userService;
    private final SchoolService schoolService;
    private final SchoolAdminRepository schoolAdminRepository;

    public SchoolAdminServiceImpl(UserService userService,
                                  SchoolService schoolService,
                                  SchoolAdminRepository schoolAdminRepository) {
        this.userService = userService;
        this.schoolService = schoolService;
        this.schoolAdminRepository = schoolAdminRepository;
    }

    @Override
    public SchoolAdmin findById(UUID id) {
        return schoolAdminRepository.findById(id).orElseThrow(
                () -> new BadCredentialsException("Bad credentials")
        );
    }

    @Override
    public SchoolAdmin findByUserId(UUID id) {
        return schoolAdminRepository.findByUserId_Id(id).orElseThrow(
                () -> new BadCredentialsException("Bad credentials")
        );
    }

    @Override
    public UUID findSchoolIdByUserId(UUID userId) {
        return schoolAdminRepository.findSchoolIdByUserId(userId).orElseThrow(
                () -> new NotFoundObjectException("Not found schoolAdmin")
        );
    }

    @Override
    @Transactional
    public UUID saveSchoolAdmin(User user, School school) {
        user = userService.saveSchoolAdmin(user);
        school = schoolService.save(school);
        SchoolAdmin schoolAdmin = new SchoolAdmin(null, user, school);
        schoolAdmin = schoolAdminRepository.save(schoolAdmin);
        return schoolAdmin.getId();
    }
}
