package com.system.application.core.schoolsubscription.service;

import com.system.application.core.schoolsubscription.SchoolSubscription;
import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionDetailResponse;
import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionRequest;
import com.system.application.core.schoolsubscription.dto.SchoolSubscriptionResponse;
import com.system.application.shared.dto.PageResponse;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface SchoolSubscriptionService {
    PageResponse<SchoolSubscriptionResponse> findAllResponseBySchoolId(UUID userId, int page, int size);
    SchoolSubscription findById(UUID schoolSubscriptionId);
    SchoolSubscriptionDetailResponse findDetailById(UUID userId, UUID schoolSubscriptionId);
    SchoolSubscription create(UUID userId, SchoolSubscriptionRequest request);
    void ActiveById(UUID userId, UUID schoolSubscriptionId);
    void cancelById(UUID userId, UUID schoolSubscriptionId);
}
