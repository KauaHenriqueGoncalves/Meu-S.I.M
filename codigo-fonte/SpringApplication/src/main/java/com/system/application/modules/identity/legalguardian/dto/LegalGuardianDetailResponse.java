package com.system.application.modules.identity.legalguardian.dto;

import com.system.application.modules.identity.legalguardian.LegalGuardian;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record LegalGuardianDetailResponse(

        UUID id,
        String username,
        String email,
        String cpf,
        String phoneNumber,
        String address,
        Boolean isActive,
        String degreeOfKinship

) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static LegalGuardianDetailResponse of(LegalGuardian lg) {
        return new LegalGuardianDetailResponse(
                lg.getId(),
                lg.getUser().getUsername(),
                lg.getUser().getEmail(),
                lg.getUser().getCpf(),
                lg.getUser().getPhoneNumber(),
                lg.getUser().getAddress(),
                lg.getUser().getActive(),
                lg.getDegreeOfKinship()
        );
    }
}
