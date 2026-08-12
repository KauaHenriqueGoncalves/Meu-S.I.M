package com.system.application.modules.identity.systemadmin.dto;

import com.system.application.modules.identity.systemadmin.SystemAdmin;
import java.util.List;
import java.util.UUID;

public record SystemAdminSimpleViewResponseDTO(
        UUID id,
        String username,
        String email
) {
    public static List<SystemAdminSimpleViewResponseDTO> of(List<SystemAdmin> list) {
        return list.stream()
                .map(SystemAdminSimpleViewResponseDTO::of)
                .toList();
    }

    public static SystemAdminSimpleViewResponseDTO of(SystemAdmin s) {
        return new SystemAdminSimpleViewResponseDTO(
                s.getId(),
                s.getUser().getUsername(),
                s.getUser().getEmail()
        );
    }
}
