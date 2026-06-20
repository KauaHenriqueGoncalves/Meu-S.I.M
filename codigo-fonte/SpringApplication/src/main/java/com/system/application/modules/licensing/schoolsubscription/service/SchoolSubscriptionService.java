package com.system.application.modules.licensing.schoolsubscription.service;

import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.licensing.schoolsubscription.dto.*;
import com.system.application.shared.dto.PageResponse;

import java.util.UUID;

public interface SchoolSubscriptionService {
    PageResponse<SchoolSubscriptionResponse> findAllResponseBySchoolId(UUID userId, int page, int size);
    SchoolSubscription findById(UUID schoolSubscriptionId);
    SchoolSubscription findActiveSubscriptionBySchoolId(UUID schoolId);
    SchoolSubscriptionDetailResponse findDetailById(UUID userId, UUID schoolSubscriptionId);
    SchoolSubscriptionDetailResponse findDetailById(UUID schoolSubscriptionId);
    SubscriptionInfoResponse findActiveSubscription(UUID userId);
    SchoolSubscriptionCheckoutResponse createCheckout(UUID userId, SchoolSubscriptionRequest request);
    String activeById(UUID schoolSubscriptionId, PaymentResult paymentResult);
    void cancelById(UUID userId, UUID schoolSubscriptionId);
    void reactiveById(UUID userId, UUID schoolSubscriptionId);
}
