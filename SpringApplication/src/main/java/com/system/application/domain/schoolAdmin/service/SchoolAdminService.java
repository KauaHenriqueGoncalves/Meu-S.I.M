package com.system.application.domain.schoolAdmin.service;

import com.system.application.domain.school.School;
import com.system.application.domain.school.service.SchoolService;
import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.schoolAdmin.repository.SchoolAdminRepository;
import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public final class SchoolAdminService {
    private final UserService userService;
    private final SchoolService schoolService;
    private final SchoolAdminRepository schoolAdminRepository;

    public SchoolAdminService(UserService userService,
                              SchoolService schoolService,
                              SchoolAdminRepository schoolAdminRepository) {
        this.userService = userService;
        this.schoolService = schoolService;
        this.schoolAdminRepository = schoolAdminRepository;
    }

    public UUID createSchoolAdmin(User user, School school) {
        user = userService.saveSchoolAdmin(user);
        school = schoolService.save(school);
        SchoolAdmin schoolAdmin = new SchoolAdmin(null, user, school);
        schoolAdmin = schoolAdminRepository.save(schoolAdmin);
        return schoolAdmin.getId();
    }

}
