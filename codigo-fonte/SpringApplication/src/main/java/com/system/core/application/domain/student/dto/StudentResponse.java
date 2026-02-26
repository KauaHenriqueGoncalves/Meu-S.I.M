package com.system.core.application.domain.student.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record StudentResponse(
        UUID id,
        String name,
        LocalDate dateOfBirth,
        String grade
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
