package com.system.application.core.schoolplan.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record SchoolPlanResponse(
        UUID id,
        String name,
        BigDecimal monthlyPrice,
        Integer maxStudents,
        Integer maxCollaborators,
        Integer maxLegalGuardian,
        Integer maxSchoolAdmin,
        Boolean isActive
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
