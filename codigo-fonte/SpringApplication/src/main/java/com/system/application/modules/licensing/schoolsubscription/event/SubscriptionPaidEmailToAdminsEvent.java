package com.system.application.modules.licensing.schoolsubscription.event;

import java.util.UUID;

public record SubscriptionPaidEmailToAdminsEvent(
        UUID subscriptionId,
        UUID schoolId
) { }
