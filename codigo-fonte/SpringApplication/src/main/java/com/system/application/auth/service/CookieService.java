package com.system.application.auth.service;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public interface CookieService {
    ResponseCookie createCookie(String path, String key, String value, Duration duration);
}
