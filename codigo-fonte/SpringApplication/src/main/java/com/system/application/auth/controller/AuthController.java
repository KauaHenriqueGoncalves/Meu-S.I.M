package com.system.application.auth.controller;

import com.system.application.auth.dto.AdminLoginRequest;
import com.system.application.auth.dto.LoginRequest;
import com.system.application.auth.dto.LoginResponse;
import com.system.application.auth.service.CookieService;
import com.system.application.auth.service.JwtService;
import com.system.application.auth.service.LoginService;
import com.system.application.auth.service.RefreshService;
import com.system.application.auth.token.TokenResponse;
import com.system.application.integration.captcha.dto.CaptchaRequest;
import com.system.application.integration.captcha.service.CaptchaService;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.service.UserService;
import com.system.application.shared.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final RefreshService  refreshService;
    private final JwtService jwtService;
    private final UserService userService;
    private final CookieService cookieService;
    private final CaptchaService captchaService;

    public AuthController(
            LoginService loginService,
            RefreshService refreshService,
            JwtService jwtService,
            UserService userService,
            CookieService cookieService,
            @Qualifier("turnstile") CaptchaService captchaService
    ) {
        this.loginService = loginService;
        this.refreshService = refreshService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.cookieService = cookieService;
        this.captchaService = captchaService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        if (!captchaService.validate(loginRequest.captchaRequest().captchaToken())) {
            throw new AccessDeniedException("Verificação de segurança falhou!");
        }
        LoginResponse loginResponse = loginService.login(loginRequest);
        String accessToken = jwtService.generateAccessToken(loginResponse);
        String refreshToken = jwtService.generateRefreshToken(loginResponse);
        ResponseCookie cookie =
                cookieService.createCookie("/api/v1/auth/refresh", "refreshToken", refreshToken, Duration.ofDays(7));
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
                cookieService.createCookie("/api/v1/auth/refresh", "refreshToken", refreshToken, Duration.ofDays(7));
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(new TokenResponse(accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue("refreshToken") String refreshToken
    ) {
        //TODO: implements Redis

        // TODO: Criar serviço para refresh

        // TODO: Refresh vazio, retorne um erro no body

        String accessToken = refreshService.getAccessToken(refreshToken);
        return ResponseEntity.ok(new TokenResponse(accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response
    ) {
        //TODO: RefreshToken still valid, the future implements Redis
        ResponseCookie cookie =
                cookieService.createCookie("/api/v1/auth/refresh", "refreshToken", "", Duration.ofDays(7));
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }
}
