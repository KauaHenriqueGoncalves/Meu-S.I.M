package com.system.application.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticatedUserServiceImpl implements AuthenticatedUserService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticatedUserServiceImpl.class);

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
}
