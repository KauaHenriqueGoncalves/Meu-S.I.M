package com.system.application.modules.licensing.billingdiscount.dto;

import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record BillingDiscountRequest(
        @NotNull(message = "O número de meses é obrigatório")
        @Min(value = 1, message = "O número de meses deve ser no mínimo 1")
        @Max(value = 12, message = "O número de meses deve ser no máximo 12")
        Integer months,

        @NotNull(message = "O percentual de desconto é obrigatório")
        @DecimalMin(value = "0.00", message = "O percentual de desconto não pode ser negativo")
        @DecimalMax(value = "100.00", message = "O percentual de desconto não pode ser maior que 100%")
        @Digits(integer = 3, fraction = 2, message = "O percentual de desconto deve ter no máximo 3 dígitos inteiros e 2 casas decimais")
        BigDecimal discountPercent
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
