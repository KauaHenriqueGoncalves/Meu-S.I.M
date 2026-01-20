package com.system.application.domain.legalGuardian.service;

import com.system.application.domain.legalGuardian.dto.LegalGuardianRequest;
import com.system.application.domain.user.User;

import java.util.UUID;

public interface LegalGuardianService {
    UUID saveLegalGuardian(User user, UUID adminId, LegalGuardianRequest legalGuardianRequest);
}
