package com.system.application.modules.school.facade;

import com.system.application.modules.school.School;
import com.system.application.modules.school.dto.CreateSchoolRequestDTO;
import com.system.application.modules.school.dto.SchoolResponseDTO;
import com.system.application.modules.school.dto.UpdateSchoolRequestDTO;
import java.util.UUID;

public interface SchoolFacade {
    School getEntityById(UUID id);
    School getEntityByUserId(UUID userId);
    School getEntityByOwnerId();
    SchoolResponseDTO getById(UUID id);
    SchoolResponseDTO getByOwnerId();
    School create(CreateSchoolRequestDTO dto);
    School update(UUID id, UpdateSchoolRequestDTO dto);
}
