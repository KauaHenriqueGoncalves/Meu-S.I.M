package com.system.application.modules.identity.profile.schooladmin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.system.application.integration.captcha.dto.CaptchaRequestDTO;
import com.system.application.modules.school.dto.CreateSchoolRequestDTO;
import com.system.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CreateNewSchoolRequestDTO(
        @Valid
        @NotNull
        @JsonProperty("user")
        CreateUserRequestDTO createUserDto,

        @Valid
        @NotNull
        @JsonProperty("school")
        CreateSchoolRequestDTO createSchoolDto,

        @Valid
        @NotNull
        @JsonProperty("captchaToken")
        CaptchaRequestDTO captchaRequestDto
) { }
