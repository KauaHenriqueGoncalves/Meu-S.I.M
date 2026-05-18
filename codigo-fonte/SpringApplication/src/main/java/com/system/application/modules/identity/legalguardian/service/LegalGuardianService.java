package com.system.application.modules.identity.legalguardian.service;

import com.system.application.modules.identity.legalguardian.LegalGuardian;
import com.system.application.modules.identity.legalguardian.dto.*;
import com.system.application.modules.identity.user.dto.PasswordRequest;
import com.system.application.modules.identity.user.dto.UserRequest;
import com.system.application.shared.dto.PageResponse;

import java.util.UUID;

public interface LegalGuardianService {
    PageResponse<LegalGuardianResponse> findAllResponseBySchool(UUID userId, String name, int page, int size);
    LegalGuardian findById(UUID legalGuardianId);
    LegalGuardianDetailResponse findResponseDetailById(UUID legalGuardianId);
    LegalGuardian save(UUID userId, UserRequest userRequest, LegalGuardianRequest legalGuardianRequest);
    void update(UUID userId, UUID legalGuardianId, UpdateLegalGuardianRequest updateRequest);
    void updatePassword(UUID userId, UUID legalGuardianId, PasswordRequest updateRequest);
    void deleteById(UUID userId, UUID legalGuardianId);
}
