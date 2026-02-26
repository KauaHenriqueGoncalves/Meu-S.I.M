package com.system.application.domain.student.dto;

import com.system.application.domain.legalguardian.dto.LegalGuardianResponse;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record StudentDetailResponse(
        UUID id,
        String name,
        LocalDate dateOfBirth,
        String grade,
        LegalGuardianResponse legalGuardianResponse
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
