package com.system.application.domain.schooladmin.service;

import com.system.application.domain.role.Role;
import com.system.application.domain.school.School;
import com.system.application.domain.school.dto.SchoolRequest;
import com.system.application.domain.school.service.SchoolService;
import com.system.application.domain.schooladmin.SchoolAdmin;
import com.system.application.domain.schooladmin.repository.SchoolAdminRepository;
import com.system.application.domain.user.User;
import com.system.application.domain.user.dto.UserRequest;
import com.system.application.domain.user.service.UserService;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SchoolAdminServiceImpl implements SchoolAdminService {
    private final SchoolAdminRepository schoolAdminRepository;
    private final UserService userService;
    private final SchoolService schoolService;

    public SchoolAdminServiceImpl(
            SchoolAdminRepository schoolAdminRepository,
            UserService userService,
            SchoolService schoolService
    ) {
        this.schoolAdminRepository = schoolAdminRepository;
        this.userService = userService;
        this.schoolService = schoolService;
    }

    @Override
    public SchoolAdmin findById(UUID id) {
        return schoolAdminRepository.findById(id)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
    }

    @Override
    public SchoolAdmin findByUserId(UUID userId) {
        return schoolAdminRepository.findByUserId(userId)
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
    }

    @Override
    public UUID findSchoolIdByUserId(UUID userId) {
        return schoolAdminRepository.findSchoolIdByUserId(userId)
                .orElseThrow(() -> new NotFoundObjectException("Not found schoolAdmin"));
    }

    @Override
    @Transactional
    public SchoolAdmin save(UserRequest userRequest, SchoolRequest schoolRequest) {
        User user = userService.registerUserWithRole(userRequest, Role.Values.SCHOOL_ADMIN);
        School school = schoolService.save(schoolRequest);
        SchoolAdmin schoolAdmin = new SchoolAdmin(null, user, school);
        schoolAdmin = schoolAdminRepository.save(schoolAdmin);
        return schoolAdmin;
    }
}
