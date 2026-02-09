package com.system.application.auth.service;

import com.system.application.auth.dto.AdminLoginRequest;
import com.system.application.auth.dto.LoginRequest;
import com.system.application.auth.dto.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse login(AdminLoginRequest adminLoginRequest);
}
