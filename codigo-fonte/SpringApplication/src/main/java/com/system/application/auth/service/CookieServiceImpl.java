package com.system.application.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public final class CookieServiceImpl implements CookieService {
    @Value("${app.secure-cookies}")
    private boolean secureCookies;

    @Override
    public ResponseCookie createCookie(String path, String key, String value, Duration duration) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(secureCookies) // true em HTTPS
                .path(path)
                .maxAge(duration)
                .sameSite(secureCookies ? "None" : "Lax")
                .build();
    }
}
