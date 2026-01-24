package com.system.application.domain.schoolAdmin.dto;

import com.system.application.domain.school.dto.SchoolRequest;
import com.system.application.domain.user.dto.UserRequest;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;

public record CreateSchoolAdminRequest(
        @Valid UserRequest userRequest,
        @Valid SchoolRequest schoolRequest
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
