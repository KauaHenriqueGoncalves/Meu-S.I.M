package com.meusim.application.modules.licensing.schoolsubscription.dto;

import com.meusim.application.modules.licensing.schoolsubscription.SchoolSubscription;

public record SubscriptionInfoResponse(

        String planName,
        Integer maxStudents,
        Integer maxCollaborators,
        Integer maxLegalGuardian,
        Integer maxSchoolAdmin

) {
    public static SubscriptionInfoResponse of(SchoolSubscription s) {
        return new SubscriptionInfoResponse(
                s.getPlanName(),
                s.getMaxStudents(),
                s.getMaxCollaborators(),
                s.getMaxLegalGuardian(),
                s.getMaxSchoolAdmin()
        );
    }
}
