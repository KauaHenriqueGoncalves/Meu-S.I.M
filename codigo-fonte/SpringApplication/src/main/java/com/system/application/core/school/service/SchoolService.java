package com.system.application.core.school.service;

import com.system.application.core.school.School;
import com.system.application.core.school.dto.SchoolRequest;

import java.util.UUID;

public interface SchoolService {
    School findById(UUID schoolId);
    School findByUserId(UUID userId);
    School save(SchoolRequest request);
}
