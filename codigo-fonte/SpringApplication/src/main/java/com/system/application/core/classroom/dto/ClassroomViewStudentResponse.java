package com.system.application.core.classroom.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record ClassroomViewStudentResponse(
        UUID id,
        String name
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
