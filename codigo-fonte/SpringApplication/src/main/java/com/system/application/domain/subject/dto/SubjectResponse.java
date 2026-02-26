package com.system.application.domain.subject.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record SubjectResponse(
        UUID id,
        String name
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
