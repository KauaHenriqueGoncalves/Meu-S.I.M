package com.system.application.modules.identity.schooladmin.dto;

import com.system.application.integration.captcha.dto.CaptchaRequest;
import com.system.application.modules.school.dto.SchoolRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;

public record CreateSchoolAdminRequest(

        @Valid UserRequest userRequest,
        @Valid SchoolRequest schoolRequest,
        @Valid CaptchaRequest captchaRequest

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
