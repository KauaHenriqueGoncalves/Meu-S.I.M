package com.system.application.auth.controller;

import com.system.application.auth.dto.AdminLoginRequest;
import com.system.application.auth.dto.LoginRequest;
import com.system.application.auth.dto.LoginResponse;
import com.system.application.auth.service.CookieService;
import com.system.application.auth.service.JwtService;
import com.system.application.auth.service.LoginService;
import com.system.application.auth.token.TokenResponse;
import com.system.application.domain.user.User;
import com.system.application.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public final class AuthController {
    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserService userService;
    private final CookieService cookieService;

    public AuthController(
            LoginService loginService,
            JwtService jwtService,
            UserService userService,
            CookieService cookieService
    ) {
        this.loginService = loginService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.cookieService = cookieService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = loginService.login(loginRequest);
        String accessToken = jwtService.generateAccessToken(loginResponse);
        String refreshToken = jwtService.generateRefreshToken(loginResponse);
        ResponseCookie cookie =
                cookieService.createCookie("/auth/refresh", "refreshToken", refreshToken, Duration.ofDays(7));
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(new TokenResponse(accessToken));
    }

    @PostMapping("/login/admin")
    public ResponseEntity<TokenResponse> loginAdmin(
            @RequestBody @Valid AdminLoginRequest adminLoginRequest,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = loginService.login(adminLoginRequest);
        String accessToken = jwtService.generateAccessToken(loginResponse);
        String refreshToken = jwtService.generateRefreshToken(loginResponse);
        ResponseCookie cookie =
                cookieService.createCookie("/auth/refresh", "refreshToken", refreshToken, Duration.ofDays(7));
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(new TokenResponse(accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue("refreshToken") String refreshToken
    ) {
        //TODO: implements Redis
        Jwt jwt = jwtService.decode(refreshToken);
        String id = jwt.getSubject();
        User user = userService.findById(UUID.fromString(id));
        String accessToken = jwtService.generateAccessToken(new LoginResponse(user.getId(), user.getRole()));
        return ResponseEntity.ok(new TokenResponse(accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response
    ) {
        //TODO: RefreshToken still valid, the future implements Redis
        ResponseCookie cookie = cookieService.createCookie("/auth/refresh", "refreshToken", "", Duration.ofDays(7));
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }
}
