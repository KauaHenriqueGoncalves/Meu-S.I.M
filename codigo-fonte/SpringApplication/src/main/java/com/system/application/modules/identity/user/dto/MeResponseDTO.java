package com.system.application.modules.identity.user.dto;

import com.system.application.modules.identity.user.User;

import java.util.UUID;

public record MeResponseDTO(
        UUID id,
        String username,
        String email
) {
    public static MeResponseDTO of(User u) {
        return new MeResponseDTO(
                u.getId(),
                u.getUsername(),
                u.getEmail()
        );
    }
}
