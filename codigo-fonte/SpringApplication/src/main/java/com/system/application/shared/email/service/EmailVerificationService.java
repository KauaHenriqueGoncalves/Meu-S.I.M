package com.system.application.shared.email.service;

import java.util.UUID;

public interface EmailVerificationService {
    String createOrRefreshToken(UUID userId);
    void validateUser(String token);
}
