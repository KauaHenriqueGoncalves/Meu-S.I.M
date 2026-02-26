package com.system.core.application.auth.service;

import com.system.core.application.auth.dto.LoginResponse;
import com.system.core.application.domain.role.Role;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public final class JwtServiceImpl implements JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtServiceImpl(
            JwtEncoder jwtEncoder,
            JwtDecoder jwtDecoder
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public String generateAccessToken(LoginResponse loginResponse) {
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("api-system-application")
                .audience(List.of("system-web"))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(900)) //900 = 15 min
                .subject(loginResponse.id().toString())
                .claims(claims -> {
                    claims.put(
                            "scope",
                            loginResponse.role()
                                    .stream()
                                    .map(Role::getName)
                                    .collect(Collectors.joining(" "))
                    );
                    claims.put("type", "access_token");
                })
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    @Override
    public String generateRefreshToken(LoginResponse loginResponse) {
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("api-system-application")
                .audience(List.of("system-web"))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(604800)) //604800 == 7days
                .subject(loginResponse.id().toString())
                .claims(claims -> {
                    claims.put("type", "refresh_token");
                })
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    @Override
    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }
}
