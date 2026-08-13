package com.system.application.modules.identity.systemadmin.dto;

import com.system.application.modules.identity.systemadmin.SystemAdmin;
import java.util.UUID;

public record SystemAdminDetailViewResponseDTO(
        UUID id,
        String username,
        String email,
        String cpf,
        String phoneNumber,
        String address,
        Boolean isActive
) {
    public static SystemAdminDetailViewResponseDTO of(SystemAdmin s) {
        return new SystemAdminDetailViewResponseDTO(
                s.getId(),
                s.getUser().getUsername(),
                s.getUser().getEmail(),
                s.getUser().getCpf(),
                s.getUser().getPhoneNumber(),
                s.getUser().getAddress(),
                s.getUser().getActive()
        );
    }
}
