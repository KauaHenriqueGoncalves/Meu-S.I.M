package com.system.application.domain.legalGuardian.mapper;

import com.system.application.domain.legalGuardian.LegalGuardian;
import com.system.application.domain.legalGuardian.dto.LegalGuardianDetailResponse;

public interface LegalGuardianMapper {
    LegalGuardianDetailResponse toDtoDetail(LegalGuardian legalGuardian);
}
