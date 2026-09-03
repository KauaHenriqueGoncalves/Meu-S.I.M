package com.meusim.application.auth.service;

public interface RefreshService {
    String getAccessToken(String refreshToken);
}
