package com.system.core.application.domain.schooladmin.service;

import com.system.core.application.domain.school.dto.SchoolRequest;
import com.system.core.application.domain.schooladmin.SchoolAdmin;
import com.system.core.application.domain.user.dto.UserRequest;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SchoolAdminService {
    SchoolAdmin findById(UUID id);
    SchoolAdmin findByUserId(UUID userId);
    UUID findSchoolIdByUserId(@Param("userId") UUID userId);
    SchoolAdmin save(UserRequest userRequest, SchoolRequest schoolRequest);
}
