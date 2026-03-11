package com.system.application.core.schoolsubscription.dto;

import java.math.BigDecimal;

public record SchoolSubscriptionCheckoutResponse(
        String title,
        BigDecimal planPrice,
        Integer months,
        String initPoint,
        String preferenceId
) { }
