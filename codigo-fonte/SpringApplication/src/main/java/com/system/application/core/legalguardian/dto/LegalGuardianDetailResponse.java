package com.system.application.core.legalguardian.dto;

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
}
