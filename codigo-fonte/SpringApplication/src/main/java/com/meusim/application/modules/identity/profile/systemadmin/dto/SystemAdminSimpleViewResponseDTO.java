package com.meusim.application.modules.identity.profile.systemadmin.dto;

import com.meusim.application.modules.identity.profile.systemadmin.SystemAdmin;
import java.util.List;
import java.util.UUID;

public record SystemAdminSimpleViewResponseDTO(
        UUID id,
        String username,
        String email
) {
    public static SystemAdminSimpleViewResponseDTO of(SystemAdmin s) {
        return new SystemAdminSimpleViewResponseDTO(
                s.getId(),
                s.getUser().getUsername(),
                s.getUser().getEmail()
        );
    }

    public static List<SystemAdminSimpleViewResponseDTO> of(List<SystemAdmin> list) {
        return list.stream().map(SystemAdminSimpleViewResponseDTO::of).toList();
    }
}
