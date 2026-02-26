package com.system.application.domain.school.service;

import com.system.application.domain.school.School;
import com.system.application.domain.school.dto.SchoolRequest;

import java.util.UUID;

public interface SchoolService {
    School findById(UUID schoolId);
    School findByUserId(UUID userId);
    School save(SchoolRequest request);
}
