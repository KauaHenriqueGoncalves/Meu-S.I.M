package com.meusim.application.modules.identity.profile.schooladmin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meusim.application.integration.captcha.dto.CaptchaRequestDTO;
import com.meusim.application.modules.school.dto.CreateSchoolRequestDTO;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
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
        @JsonProperty("captcha")
        CaptchaRequestDTO captchaRequestDto
) { }
