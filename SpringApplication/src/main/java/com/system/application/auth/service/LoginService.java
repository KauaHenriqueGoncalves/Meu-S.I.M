package com.system.application.auth.service;

import com.system.application.auth.dto.AdminLoginRequest;
import com.system.application.auth.dto.LoginRequest;
import com.system.application.auth.dto.LoginResponse;
import com.system.application.domain.user.User;

public interface LoginService {
    User login(LoginRequest loginRequest);
    LoginResponse login(AdminLoginRequest adminLoginRequest);
}
