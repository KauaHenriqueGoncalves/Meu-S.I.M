package com.system.application.modules.school.service;

import com.system.application.modules.school.dto.SchoolCapacityResponseDTO;

import java.util.UUID;

public interface SchoolCapacityQuery {
    SchoolCapacityResponseDTO getCapacity(UUID schoolId);
}
