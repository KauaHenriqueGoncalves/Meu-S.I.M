package com.system.application.modules.licensing.schoolsubscription.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record SchoolSubscriptionRequest(

        @NotNull(message = "O plano escolar é obrigatório")
        UUID schoolPlanId,

        @NotNull(message = "O número de meses é obrigatório")
        @Min(value = 1, message = "O número de meses deve ser no mínimo 1")
        @Max(value = 12, message = "O número de meses deve ser no máximo 12")
        Integer months

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
