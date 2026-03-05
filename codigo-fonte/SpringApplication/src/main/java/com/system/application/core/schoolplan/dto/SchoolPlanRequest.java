package com.system.application.core.schoolplan.dto;

import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record SchoolPlanRequest(
        @NotBlank
        @Size(max = 50)
        String name,

        @NotNull
        @DecimalMin(value = "0.00")
        @DecimalMax(value = "999.00")
        @Digits(integer = 6, fraction = 2)
        BigDecimal monthlyPrice,

        @NotNull
        @Min(1)
        Integer maxStudents,

        @NotNull
        @Min(1)
        Integer maxCollaborators,

        @NotNull
        @Min(1)
        Integer maxLegalGuardian,

        @NotNull
        @Min(1)
        Integer maxSchoolAdmin
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
