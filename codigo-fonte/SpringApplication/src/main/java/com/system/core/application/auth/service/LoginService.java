package com.system.core.application.auth.service;

import com.system.core.application.auth.dto.AdminLoginRequest;
import com.system.core.application.auth.dto.LoginRequest;
import com.system.core.application.auth.dto.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse login(AdminLoginRequest adminLoginRequest);
}
