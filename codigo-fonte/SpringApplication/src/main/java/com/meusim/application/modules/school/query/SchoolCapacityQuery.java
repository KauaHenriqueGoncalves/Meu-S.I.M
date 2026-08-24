package com.meusim.application.modules.school.query;

import com.meusim.application.modules.school.dto.SchoolCapacityResponseDTO;
import java.util.UUID;

public interface SchoolCapacityQuery {
    SchoolCapacityResponseDTO getCapacity(UUID schoolId);
}
