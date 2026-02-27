package com.system.application.core.schooladmin.dto;

import com.system.application.core.school.dto.SchoolRequest;
import com.system.application.core.user.dto.UserRequest;
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
