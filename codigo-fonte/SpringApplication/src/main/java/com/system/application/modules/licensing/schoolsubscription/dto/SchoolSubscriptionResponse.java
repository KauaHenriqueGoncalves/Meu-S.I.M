package com.system.application.modules.licensing.schoolsubscription.dto;

import com.system.application.modules.licensing.schoolsubscription.enums.SubscriptionStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record SchoolSubscriptionResponse(
        UUID id,
        String planName,
        LocalDate startDate,
        LocalDate endDate,
        SubscriptionStatus status
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
