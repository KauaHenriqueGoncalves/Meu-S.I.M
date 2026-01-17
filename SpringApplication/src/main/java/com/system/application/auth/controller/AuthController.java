package com.system.application.auth.controller;

import com.system.application.auth.dto.AdminLoginRequest;
import com.system.application.auth.dto.LoginRequest;
import com.system.application.auth.dto.LoginResponse;
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

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public final class AuthController {
    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(LoginService loginService,
                          JwtService jwtService,
                          UserService userService) {
        this.loginService = loginService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody @Valid LoginRequest loginRequest,
                                               HttpServletResponse response) {
        User loginResponse = loginService.login(loginRequest);
        //TODO
//        String acessToken = jwtService.generateAccessToken(loginResponse);
//        String refreshToken = jwtService.generateRefreshToken(loginResponse);
//        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
//                .httpOnly(true)
//                .secure(true) // true em HTTPS
//                .path("/auth/refresh")
//                .maxAge(Duration.ofDays(7))
//                .sameSite("Strict")
//                .build();
//        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//        return ResponseEntity.ok(new TokenResponse(acessToken));
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/login/admin")
    public ResponseEntity<LoginResponse> loginAdmin(@RequestBody @Valid AdminLoginRequest adminLoginRequest,
                                                    HttpServletResponse response) {
        LoginResponse loginResponse = loginService.login(adminLoginRequest);

        //TODO: Implements COOKIE with Refresh Token

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
        //TODO: implements Redis
        Jwt jwt = jwtService.decode(refreshToken);
        String id = jwt.getSubject();
        User user = userService.findById(UUID.fromString(id));
        String acessToken = jwtService.generateAccessToken(new LoginResponse(user.getId(), user.getRole()));
        return ResponseEntity.ok(new TokenResponse(acessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        //TODO: RefreshToken still valid, the future implements Redis
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }
}
