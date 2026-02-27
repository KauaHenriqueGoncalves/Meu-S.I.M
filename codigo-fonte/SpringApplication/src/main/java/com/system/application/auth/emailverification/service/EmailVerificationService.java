package com.system.application.auth.emailverification.service;

import java.util.UUID;

public interface EmailVerificationService {
    String createOrRefreshToken(UUID userId);
    void validateUser(String token);
}
