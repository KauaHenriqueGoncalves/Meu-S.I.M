package com.system.application.shared.email.service;

import com.system.application.domain.user.User;

public interface EmailVerificationService {
    String createToken(User user);
    void validateUser(String token);
}
