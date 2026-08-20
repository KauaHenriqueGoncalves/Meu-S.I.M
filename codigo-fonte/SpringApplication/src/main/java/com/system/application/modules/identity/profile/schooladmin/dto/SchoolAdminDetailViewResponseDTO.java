package com.system.application.modules.identity.profile.schooladmin.dto;

import com.system.application.modules.identity.profile.schooladmin.SchoolAdmin;
import java.util.UUID;

public record SchoolAdminDetailViewResponseDTO(
        UUID id,
        String username,
        String email,
        String cpf,
        String phoneNumber,
        String address,
        Boolean isActive
) {
    public static SchoolAdminDetailViewResponseDTO of(SchoolAdmin sa) {
        return new SchoolAdminDetailViewResponseDTO(
                sa.getId(),
                sa.getUser().getUsername(),
                sa.getUser().getEmail(),
                sa.getUser().getCpf(),
                sa.getUser().getPhoneNumber(),
                sa.getUser().getAddress(),
                sa.getUser().getActive()
        );
    }
}
