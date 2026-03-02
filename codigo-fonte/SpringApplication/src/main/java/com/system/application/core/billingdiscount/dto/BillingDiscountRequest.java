package com.system.application.core.billingdiscount.dto;

import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record BillingDiscountRequest(
        @NotNull
        @Min(1)
        @Max(12)
        Integer months,

        @NotNull
        @DecimalMin(value = "0.00")
        @DecimalMax(value = "100.00")
        @Digits(integer = 3, fraction = 2)
        BigDecimal discountPercent
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
