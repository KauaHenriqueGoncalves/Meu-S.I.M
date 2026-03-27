package com.system.application.modules.licensing.billingdiscount.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record BillingDiscountResponse(

        UUID id,
        Integer months,
        BigDecimal discountPercent

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
