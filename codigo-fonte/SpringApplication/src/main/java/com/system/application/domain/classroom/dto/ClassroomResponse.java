package com.system.application.domain.classroom.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record ClassroomResponse(
        UUID id,
        String classTypeName,
        String subjectName,
        String name
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
