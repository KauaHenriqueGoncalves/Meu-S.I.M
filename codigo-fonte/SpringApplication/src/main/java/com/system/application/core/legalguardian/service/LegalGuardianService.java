package com.system.application.core.legalguardian.service;

import com.system.application.core.legalguardian.LegalGuardian;
import com.system.application.core.legalguardian.dto.*;
import com.system.application.core.user.dto.UserRequest;
import com.system.application.shared.dto.PageResponse;

import java.util.UUID;

public interface LegalGuardianService {
    PageResponse<LegalGuardianResponse> findAllResponseBySchool(UUID userId, int page, int size);
    LegalGuardian findById(UUID legalGuardianId);
    LegalGuardianDetailResponse findResponseDetailById(UUID legalGuardianId);
    LegalGuardian save(UUID userId, UserRequest userRequest, LegalGuardianRequest legalGuardianRequest);
    void update(UUID userId, UUID legalGuardianId, UpdateLegalGuardianRequest updateRequest);
    void updatePassword(UUID userId, UUID legalGuardianId, UpdateLegalGuardianPasswordRequest updateRequest);
    void deleteById(UUID userId, UUID legalGuardianId);
}
