package com.system.application.core.schoolsubscription.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record SchoolSubscriptionRequest(
        @NotNull
        UUID schoolPlanId,

        @NotNull
        @Min(1)
        @Max(12)
        Integer months
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
