package com.system.application.domain.legalGuardian.dto;

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
        String degreeOfKinship
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
