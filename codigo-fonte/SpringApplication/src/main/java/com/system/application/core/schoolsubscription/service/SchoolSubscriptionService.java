package com.system.application.core.schoolsubscription.service;

import com.system.application.core.schoolsubscription.SchoolSubscription;
import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionRequest;

import java.util.UUID;

public interface SchoolSubscriptionService {
    SchoolSubscription findById(UUID schoolSubscriptionId);
    SchoolSubscription findSimpleById(UUID userId, UUID schoolSubscriptionId);
    SchoolSubscription save(UUID userId, SchoolSubscriptionRequest request);
    void cancelById(UUID userId, UUID schoolSubscriptionId);
}
