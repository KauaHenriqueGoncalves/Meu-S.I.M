package com.system.core.application.domain.schooladmin.dto;

import com.system.core.application.domain.school.dto.SchoolRequest;
import com.system.core.application.domain.user.dto.UserRequest;
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
