package com.meusim.application.auth.verification.service;

import java.util.UUID;

public interface EmailVerificationService {
    String createOrRefreshToken(UUID userId);
    void validateUser(String token);
}
