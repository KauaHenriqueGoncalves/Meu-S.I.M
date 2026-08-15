package com.system.application.modules.identity.schooladmin.service;

import com.system.application.modules.school.dto.CreateSchoolRequestDTO;
import com.system.application.modules.identity.schooladmin.SchoolAdmin;
import com.system.application.modules.identity.user.dto.UserRequest;

import java.util.UUID;

public interface SchoolAdminService {
    SchoolAdmin findById(UUID id);
    SchoolAdmin findByUserId(UUID userId);
    SchoolAdmin save(UserRequest userRequest, CreateSchoolRequestDTO createSchoolRequestDTO);
}
