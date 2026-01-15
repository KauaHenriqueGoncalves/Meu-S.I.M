package com.system.application.domain.schoolAdmin.dto;

import com.system.application.domain.school.dto.SchoolRequest;
import com.system.application.domain.user.dto.UserRequest;
import jakarta.validation.Valid;

public record CreateSchoolAdminRequest(
        @Valid UserRequest userRequest,
        @Valid SchoolRequest schoolRequest
) {}
