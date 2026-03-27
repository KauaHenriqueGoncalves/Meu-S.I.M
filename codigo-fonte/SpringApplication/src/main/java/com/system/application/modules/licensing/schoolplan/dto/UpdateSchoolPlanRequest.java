package com.system.application.modules.licensing.schoolplan.dto;

import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record UpdateSchoolPlanRequest(
        @NotBlank(message = "O nome do plano é obrigatório")
        @Size(max = 50, message = "O nome do plano deve ter no máximo 50 caracteres")
        String name,

        @NotNull(message = "O preço mensal é obrigatório")
        @DecimalMin(value = "0.00", message = "O preço mensal não pode ser negativo")
        @DecimalMax(value = "999.00", message = "O preço mensal não pode ser maior que R$ 999,00")
        @Digits(integer = 6, fraction = 2, message = "O preço mensal deve ter no máximo 6 dígitos inteiros e 2 casas decimais")
        BigDecimal monthlyPrice,

        @NotNull(message = "O número máximo de alunos é obrigatório")
        @Min(value = 1, message = "O número máximo de alunos deve ser no mínimo 1")
        Integer maxStudents,

        @NotNull(message = "O número máximo de colaboradores é obrigatório")
        @Min(value = 1, message = "O número máximo de colaboradores deve ser no mínimo 1")
        Integer maxCollaborators,

        @NotNull(message = "O número máximo de responsáveis é obrigatório")
        @Min(value = 1, message = "O número máximo de responsáveis deve ser no mínimo 1")
        Integer maxLegalGuardian,

        @NotNull(message = "O número máximo de administradores escolares é obrigatório")
        @Min(value = 1, message = "O número máximo de administradores escolares deve ser no mínimo 1")
        Integer maxSchoolAdmin,

        @NotNull(message = "O campo de status deve ser informado")
        Boolean isActive
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
