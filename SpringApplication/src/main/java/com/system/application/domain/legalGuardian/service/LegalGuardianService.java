package com.system.application.domain.legalGuardian.service;

import com.system.application.domain.legalGuardian.LegalGuardian;
import com.system.application.domain.legalGuardian.dto.*;
import com.system.application.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LegalGuardianService {
    Page<LegalGuardianResponse> findAllBySchoolAdminId(UUID adminId, Pageable pageable);
    LegalGuardianDetailResponse findById(UUID id);
    LegalGuardian findByIdEntity(UUID id);
    UUID saveLegalGuardian(User user, UUID adminId, LegalGuardianRequest legalGuardianRequest);
    UUID updateLegalGuardian(UUID adminId, UUID legalGuardianId, UpdateLegalGuardianRequest updateLegalGuardianRequest);
    void updatePassword(UUID adminId, UUID legalGuardianId, UpdateLegalGuardianPasswordRequest updateLegalGuardianPassword);
    void deleteById(UUID adminId, UUID legalGuardianId);
    void validateLegalGuardianBelongsToSchool(UUID adminId, UUID legalGuardianId);
}
