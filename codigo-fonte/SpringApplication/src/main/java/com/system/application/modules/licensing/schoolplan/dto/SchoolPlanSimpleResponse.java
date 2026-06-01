package com.system.application.modules.licensing.schoolplan.dto;

import com.system.application.modules.licensing.schoolplan.SchoolPlan;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record SchoolPlanSimpleResponse(
        UUID id,
        String name,
        BigDecimal monthlyPrice,
        Integer maxStudents,
        Integer maxCollaborators,
        Integer maxLegalGuardian,
        Integer maxSchoolAdmin
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static SchoolPlanSimpleResponse of(SchoolPlan sp) {
        return new SchoolPlanSimpleResponse(
                sp.getId(),
                sp.getName(),
                sp.getMonthlyPrice(),
                sp.getMaxStudents(),
                sp.getMaxCollaborators(),
                sp.getMaxLegalGuardian(),
                sp.getMaxSchoolAdmin()
        );
    }
}
