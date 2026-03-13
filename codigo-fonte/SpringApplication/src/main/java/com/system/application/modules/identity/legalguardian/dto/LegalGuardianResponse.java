package com.system.application.modules.identity.legalguardian.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record LegalGuardianResponse(

        UUID id,
        String username,
        String degreeOfKinship

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
