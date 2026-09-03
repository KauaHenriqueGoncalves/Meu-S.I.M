package com.meusim.application.modules.school.dto;

import com.meusim.application.modules.school.School;
import java.util.UUID;

public record SchoolResponseDTO(
        UUID id,
        String nameCode,
        String schoolName,
        String cnpj
) {
    public static SchoolResponseDTO of(School s) {
        return new SchoolResponseDTO(
                s.getId(),
                s.getNameCode(),
                s.getSchoolName(),
                s.getCnpj()
        );
    }
}
