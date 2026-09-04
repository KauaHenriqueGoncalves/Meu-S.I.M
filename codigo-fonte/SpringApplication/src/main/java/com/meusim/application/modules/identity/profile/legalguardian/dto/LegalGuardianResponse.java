package com.meusim.application.modules.identity.profile.legalguardian.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record LegalGuardianResponse(

        UUID id,
        String username,
        String email,
        String degreeOfKinship

) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
