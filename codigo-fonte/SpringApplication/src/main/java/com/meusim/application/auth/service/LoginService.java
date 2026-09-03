package com.meusim.application.auth.service;

import com.meusim.application.auth.dto.AdminLoginRequest;
import com.meusim.application.auth.dto.LoginRequest;
import com.meusim.application.auth.dto.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse login(AdminLoginRequest adminLoginRequest);
}
