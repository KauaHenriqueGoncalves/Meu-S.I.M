package com.system.application.modules.licensing.billingdiscount.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record BillingDiscountToClientResponseDto(

        Integer months,
        BigDecimal discountPercent

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
