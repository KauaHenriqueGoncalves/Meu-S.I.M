package com.system.application.core.schooladmin.service;

import com.system.application.core.school.dto.SchoolRequest;
import com.system.application.core.schooladmin.SchoolAdmin;
import com.system.application.core.user.dto.UserRequest;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SchoolAdminService {
    SchoolAdmin findById(UUID id);
    SchoolAdmin findByUserId(UUID userId);
    UUID findSchoolIdByUserId(@Param("userId") UUID userId);
    SchoolAdmin save(UserRequest userRequest, SchoolRequest schoolRequest);
}
