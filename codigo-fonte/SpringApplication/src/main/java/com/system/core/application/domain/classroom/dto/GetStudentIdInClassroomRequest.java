package com.system.core.application.domain.classroom.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record GetStudentIdInClassroomRequest(
        @NotNull(message = "Selecione um estudante")
        UUID studentId
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
