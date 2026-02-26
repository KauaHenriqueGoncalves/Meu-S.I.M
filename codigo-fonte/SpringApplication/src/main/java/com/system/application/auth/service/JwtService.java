package com.system.application.auth.service;

import com.system.application.auth.dto.LoginResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtService {
    String generateAccessToken(LoginResponse loginResponse);
    String generateRefreshToken(LoginResponse loginResponse);
    Jwt decode(String token);
}
