package com.system.application.auth.service;

public interface RefreshService {
    String getAccessToken(String refreshToken);
}
