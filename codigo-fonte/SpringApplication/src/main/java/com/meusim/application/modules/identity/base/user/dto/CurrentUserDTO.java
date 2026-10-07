package com.meusim.application.modules.identity.base.user.dto;

import com.meusim.application.modules.identity.base.user.User;

import java.util.UUID;

public record CurrentUserDTO(
        UUID id, // school_admin, collaborator, legal_guardian
        String username,
        String email
) {
    public static CurrentUserDTO of(User u) {
        return new CurrentUserDTO(
                u.getId(),
                u.getUsername(),
                u.getEmail()
        );
    }
}
