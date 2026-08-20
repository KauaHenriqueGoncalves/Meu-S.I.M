package com.system.application.modules.identity.profile.legalguardian.service;

import com.system.application.modules.identity.profile.legalguardian.LegalGuardian;
import com.system.application.modules.identity.base.user.dto.PasswordRequest;
import com.system.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.system.application.modules.identity.profile.legalguardian.dto.LegalGuardianDetailResponse;
import com.system.application.modules.identity.profile.legalguardian.dto.LegalGuardianRequest;
import com.system.application.modules.identity.profile.legalguardian.dto.LegalGuardianResponse;
import com.system.application.modules.identity.profile.legalguardian.dto.UpdateLegalGuardianRequest;
import com.system.application.shared.dto.PageResponse;

import java.util.UUID;

public interface LegalGuardianService {
    PageResponse<LegalGuardianResponse> findAllResponseBySchool(UUID userId, String name, int page, int size);
    LegalGuardian findById(UUID legalGuardianId);
    LegalGuardianDetailResponse findResponseDetailById(UUID legalGuardianId);
    LegalGuardian save(UUID userId, CreateUserRequestDTO createUserRequestDTO, LegalGuardianRequest legalGuardianRequest);
    void update(UUID userId, UUID legalGuardianId, UpdateLegalGuardianRequest updateRequest);
    void updatePassword(UUID userId, UUID legalGuardianId, PasswordRequest updateRequest);
    void deleteById(UUID userId, UUID legalGuardianId);
}
