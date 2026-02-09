package com.system.application.domain.student.dto;

import com.system.application.domain.legalGuardian.dto.LegalGuardianResponse;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record StudentResponse(
        UUID id,
        String name,
        LocalDate dateOfBirth,
        String grade,
        LegalGuardianResponse legalGuardian
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
