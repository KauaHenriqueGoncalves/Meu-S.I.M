package com.system.application.modules.school.service;

import com.system.application.modules.school.School;
import com.system.application.modules.school.dto.SchoolRequest;

import java.util.UUID;

public interface SchoolService {
    School findById(UUID schoolId);
    School findByUserId(UUID userId);
    School save(SchoolRequest request);
}
