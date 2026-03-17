package com.system.application.modules.licensing.schoolsubscription.dto;

public record SubscriptionInfoResponse(
        String planName,
        Integer maxStudents,
        Integer maxCollaborators,
        Integer maxLegalGuardian,
        Integer maxSchoolAdmin
) { }
