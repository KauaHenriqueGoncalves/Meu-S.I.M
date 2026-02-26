package com.system.application.auth.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public final class CookieServiceImpl implements CookieService {
    @Override
    public ResponseCookie createCookie(String path, String key, String value, Duration duration) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(true) // true em HTTPS
                .path(path)
                .maxAge(duration)
                .sameSite("Strict")
                .build();
    }
}
