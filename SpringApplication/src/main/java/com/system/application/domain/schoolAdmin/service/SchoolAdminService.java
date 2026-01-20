package com.system.application.domain.schoolAdmin.service;

import com.system.application.domain.school.School;
import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.user.User;

import java.util.UUID;

public interface SchoolAdminService {
    SchoolAdmin findById(UUID id);
    SchoolAdmin findByUserId(UUID id);
    UUID saveSchoolAdmin(User user, School school);
}
