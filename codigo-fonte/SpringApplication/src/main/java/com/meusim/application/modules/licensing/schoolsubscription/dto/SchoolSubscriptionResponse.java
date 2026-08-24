package com.meusim.application.modules.licensing.schoolsubscription.dto;

import com.meusim.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.meusim.application.modules.licensing.schoolsubscription.enums.SubscriptionStatus;

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

    public static SchoolSubscriptionResponse of(SchoolSubscription ss) {
        return new SchoolSubscriptionResponse(
                ss.getId(),
                ss.getPlanName(),
                ss.getStartDate(),
                ss.getEndDate(),
                ss.getStatus()
        );
    }
}
