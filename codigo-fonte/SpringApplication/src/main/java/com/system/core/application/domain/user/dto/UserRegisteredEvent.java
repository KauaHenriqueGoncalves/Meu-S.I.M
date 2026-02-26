package com.system.core.application.domain.user.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
