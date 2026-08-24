package com.meusim.application.modules.school.service;

import com.meusim.application.modules.school.School;
import com.meusim.application.modules.school.dto.CreateSchoolRequestDTO;
import com.meusim.application.modules.school.dto.UpdateSchoolRequestDTO;
import java.util.UUID;

public interface SchoolService {
    School findById(UUID id);
    School findByIdWithCache(UUID id);
    School findByUserId(UUID userId);
    School findByUserIdWithCache(UUID userId);
    School findByOwnerId();
    School findByOwnerIdWithCache();
    School create(CreateSchoolRequestDTO dto);
    School update(UUID id, UpdateSchoolRequestDTO dto);
    void deleteById(UUID id);
}
