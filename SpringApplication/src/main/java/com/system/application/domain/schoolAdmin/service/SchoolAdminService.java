package com.system.application.domain.schoolAdmin.service;

import com.system.application.domain.school.School;
import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.user.User;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SchoolAdminService {
    SchoolAdmin findById(UUID id);
    SchoolAdmin findByUserId(UUID id);
    UUID findSchoolIdByUserId(@Param("userId") UUID userId);
    UUID saveSchoolAdmin(User user, School school);
}
