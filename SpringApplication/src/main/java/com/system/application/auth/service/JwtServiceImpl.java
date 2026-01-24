package com.system.application.auth.service;

import com.system.application.auth.dto.LoginResponse;
import com.system.application.domain.role.Role;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela geração e validação de tokens JWT utilizados na aplicação.
 *
 * <p>
 * Este serviço centraliza a criação de Access Token e Refresh Token,
 * definindo claims, tempo de expiração e informações necessárias para
 * autenticação e autorização.
 * </p>
 *
 * Regras gerais:
 * <ul>
 *   <li>Access Token possui curta duração e é utilizado para acesso aos recursos protegidos</li>
 *   <li>Refresh Token possui maior duração e é utilizado para renovação do Access Token</li>
 *   <li>O tipo do token é definido pela claim {@code type}</li>
 * </ul>
 */
@Service
public final class JwtServiceImpl implements JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtServiceImpl(JwtEncoder jwtEncoder,
                          JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Gera um Access Token JWT para o usuário autenticado.
     *
     * <p>
     * O Access Token possui as seguintes características:
     * <ul>
     *   <li>Tempo de expiração de 15 minutos</li>
     *   <li>Contém o identificador do usuário no {@code subject}</li>
     *   <li>Contém as roles do usuário na claim {@code scope}</li>
     *   <li>Possui a claim {@code type} com valor {@code access_token}</li>
     * </ul>
     * </p>
     *
     * @param loginResponse dados do usuário autenticado
     * @return token JWT no formato String
     */
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

    /**
     * Gera um Refresh Token JWT para o usuário autenticado.
     *
     * <p>
     * O Refresh Token possui as seguintes características:
     * <ul>
     *   <li>Tempo de expiração de 7 dias</li>
     *   <li>Utilizado exclusivamente para renovação do Access Token</li>
     *   <li>Possui a claim {@code type} com valor {@code refresh_token}</li>
     * </ul>
     * </p>
     *
     * @param loginResponse dados do usuário autenticado
     * @return token JWT no formato String
     */
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

    /**
     * Decodifica e valida um token JWT.
     *
     * <p>
     * Este método delega a validação para o {@link JwtDecoder},
     * que verifica assinatura, expiração e integridade do token.
     * </p>
     *
     * @param token token JWT no formato String
     * @return token decodificado
     */
    @Override
    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }
}
