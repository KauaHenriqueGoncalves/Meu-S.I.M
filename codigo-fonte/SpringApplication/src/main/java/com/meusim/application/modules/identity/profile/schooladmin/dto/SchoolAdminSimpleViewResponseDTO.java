package com.meusim.application.modules.identity.profile.schooladmin.dto;

import com.meusim.application.modules.identity.profile.schooladmin.SchoolAdmin;
import java.util.List;
import java.util.UUID;

public record SchoolAdminSimpleViewResponseDTO(
        UUID id,
        String username,
        String email
) {
    public static SchoolAdminSimpleViewResponseDTO of(SchoolAdmin sa) {
        return new SchoolAdminSimpleViewResponseDTO(
                sa.getId(),
                sa.getUser().getUsername(),
                sa.getUser().getEmail()
        );
    }

    public static List<SchoolAdminSimpleViewResponseDTO> of(List<SchoolAdmin> list) {
        return list.stream().map(SchoolAdminSimpleViewResponseDTO::of).toList();
    }
}
