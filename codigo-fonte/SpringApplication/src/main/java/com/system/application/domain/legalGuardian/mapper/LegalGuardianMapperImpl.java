package com.system.application.domain.legalGuardian.mapper;

import com.system.application.domain.legalGuardian.LegalGuardian;
import com.system.application.domain.legalGuardian.dto.LegalGuardianDetailResponse;
import org.springframework.stereotype.Component;

@Component
public class LegalGuardianMapperImpl implements LegalGuardianMapper {
    public LegalGuardianMapperImpl() {}

    @Override
    public LegalGuardianDetailResponse toDtoDetail(LegalGuardian legalGuardian) {
        return new LegalGuardianDetailResponse(
                legalGuardian.getId(),
                legalGuardian.getUser().getUsername(),
                legalGuardian.getUser().getEmail(),
                legalGuardian.getUser().getCpf(),
                legalGuardian.getUser().getPhoneNumber(),
                legalGuardian.getUser().getAddress(),
                legalGuardian.getDegreeOfKinship()
        );
    }
}
