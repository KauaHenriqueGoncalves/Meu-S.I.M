package com.system.application.auth.service;

import com.system.application.auth.dto.LoginResponse;
import com.system.application.modules.identity.user.User;
import com.system.application.modules.identity.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public final class RefreshServiceImpl implements RefreshService {
    private static final Logger log =
            LoggerFactory.getLogger(RefreshServiceImpl.class);

    private final JwtService jwtService;
    private final UserService userService;

    public RefreshServiceImpl(
            JwtService jwtService,
            UserService userService
    ) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public String getAccessToken(String refreshToken) {
        log.info("Tentativa de renovação de access token via refresh token.");
        Jwt jwt = jwtService.decode(refreshToken);
        String id = jwt.getSubject();
        log.info("Refresh token decodificado com sucesso. [userId={}]", id);
        User user = userService.findById(UUID.fromString(id));
        log.info("Access token gerado com sucesso. [userId={}]", user.getId());
        return jwtService.generateAccessToken(new LoginResponse(user.getId(), user.getRole()));
    }
}
