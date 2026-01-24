package com.system.application.auth.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Serviço responsável pela criação de cookies utilizados na autenticação.
 *
 * <p>
 * Atualmente utilizado para criação do cookie de Refresh Token,
 * aplicando configurações de segurança recomendadas.
 * </p>
 *
 * Regras de segurança:
 * <ul>
 *   <li>Cookie é {@code HttpOnly}, impedindo acesso via JavaScript</li>
 *   <li>Cookie é {@code Secure}, sendo enviado apenas em conexões HTTPS</li>
 *   <li>Política {@code SameSite=Strict} para evitar CSRF</li>
 * </ul>
 */
@Service
public final class CookieServiceImpl implements CookieService {

    /**
     * Cria um cookie de Refresh Token com configurações de segurança.
     *
     * @param path caminho no qual o cookie estará disponível
     * @param key nome do cookie
     * @param value valor do cookie (refresh token)
     * @param duration tempo de vida do cookie
     * @return cookie configurado para uso em autenticação segura
     */
    @Override
    public ResponseCookie createRefreshCookie(String path, String key, String value, Duration duration) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(true) // true em HTTPS
                .path(path)
                .maxAge(duration)
                .sameSite("Strict")
                .build();
    }
}
