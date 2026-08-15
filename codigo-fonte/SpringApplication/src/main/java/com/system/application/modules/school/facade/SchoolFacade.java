package com.system.application.modules.school.facade;

import com.system.application.modules.school.School;
import com.system.application.modules.school.dto.CreateSchoolRequestDTO;
import com.system.application.modules.school.dto.SchoolResponseDTO;
import com.system.application.modules.school.dto.UpdateSchoolRequestDTO;
import java.util.UUID;

public interface SchoolFacade {
    School getByIdEntity(UUID id);
    School getByIdEntityWithCache(UUID id);
    SchoolResponseDTO getById(UUID id);
    School getByUserIdEntity(UUID userId);
    School getByUserIdEntityWithCache(UUID userId);
    School getByOwnerIdEntity();
    School getByOwnerIdEntityWithCache();
    SchoolResponseDTO getByOwnerId();
    School create(CreateSchoolRequestDTO dto);
    School update(UUID id, UpdateSchoolRequestDTO dto);
    void delete(UUID id);
}
