package com.system.application.modules.licensing.schoolsubscription.dto;

import java.math.BigDecimal;

public record SchoolSubscriptionCheckoutResponse(

        String title,
        BigDecimal planPrice,
        Integer months,
        String initPoint,
        String preferenceId

) { }
