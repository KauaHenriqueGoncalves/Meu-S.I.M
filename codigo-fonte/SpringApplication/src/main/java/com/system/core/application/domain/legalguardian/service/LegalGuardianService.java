package com.system.core.application.domain.legalguardian.service;

import com.system.core.application.domain.legalguardian.LegalGuardian;
import com.system.application.domain.legalguardian.dto.*;
import com.system.core.application.domain.legalguardian.dto.*;
import com.system.core.application.domain.user.dto.UserRequest;
import com.system.core.application.shared.dto.PageResponse;

import java.util.UUID;

public interface LegalGuardianService {
    PageResponse<LegalGuardianResponse> findAllResponseBySchool(UUID userId, int page, int size);
    LegalGuardian findById(UUID legalGuardianId);
    LegalGuardianDetailResponse findResponseDetailById(UUID legalGuardianId);
    LegalGuardian save(UUID userId, UserRequest userRequest, LegalGuardianRequest legalGuardianRequest);
    void update(UUID userId, UUID legalGuardianId, UpdateLegalGuardianRequest updateRequest);
    void updatePassword(UUID userId, UUID legalGuardianId, UpdateLegalGuardianPasswordRequest updateRequest);
    void deleteById(UUID userId, UUID legalGuardianId);
    void ensureLegalGuardianBelongsToUserSchool(UUID userId, UUID legalGuardianId); // TODO: Delete it, in the future
}
