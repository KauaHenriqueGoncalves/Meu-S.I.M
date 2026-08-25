package com.meusim.application.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class AuthenticatedUserServiceImpl implements AuthenticatedUserService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticatedUserServiceImpl.class);

    /**
     * Retorna o userId do usuario que está logado na aplicação
     * */
    @Override
    public UUID getOwnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Coletando o ID da autheticacao");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("Usuario nao autenticado");
            throw new IllegalStateException("Usuário não autenticado");
        }
        return UUID.fromString(authentication.getName());
    }

    @Override
    public String getOwnerRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Coletando o Role da autheticacao");
        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Usuário sem role"));
        log.info("Role coletada com sucesso");
        return role;
    }
}
