package com.system.application.auth.token;

import java.io.Serial;
import java.io.Serializable;

public record TokenResponse(
        String accessToken
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
