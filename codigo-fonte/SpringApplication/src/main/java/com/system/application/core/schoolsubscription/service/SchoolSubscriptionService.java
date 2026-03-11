package com.system.application.core.schoolsubscription.service;

import com.system.application.core.schoolsubscription.SchoolSubscription;
import com.system.application.core.schoolsubscription.dto.*;
import com.system.application.shared.dto.PageResponse;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface SchoolSubscriptionService {
    PageResponse<SchoolSubscriptionResponse> findAllResponseBySchoolId(UUID userId, int page, int size);
    SchoolSubscription findById(UUID schoolSubscriptionId);
    SchoolSubscriptionDetailResponse findDetailById(UUID userId, UUID schoolSubscriptionId);
    SchoolSubscriptionCheckoutResponse create(UUID userId, SchoolSubscriptionRequest request);
    void activeById(UUID schoolSubscriptionId, PaymentResult paymentResult);
    void cancelById(UUID userId, UUID schoolSubscriptionId);
}
