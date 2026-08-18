package com.system.application.modules.school.facade;

import com.system.application.modules.school.School;
import com.system.application.modules.school.dto.CreateSchoolRequestDTO;
import com.system.application.modules.school.dto.SchoolResponseDTO;
import com.system.application.modules.school.dto.UpdateSchoolRequestDTO;
import com.system.application.modules.school.service.SchoolService;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SchoolFacadeImpl implements SchoolFacade {
    private final SchoolService schoolService;

    public SchoolFacadeImpl(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @Override
    public School getEntityById(UUID id) {
        return schoolService.findById(id);
    }

    @Override
    public School getEntityByUserId(UUID userId) {
        return schoolService.findByUserId(userId);
    }

    @Override
    public School getEntityByOwnerId() {
        return schoolService.findByOwnerId();
    }

    @Override
    public SchoolResponseDTO getById(UUID id) {
        School s = schoolService.findByIdWithCache(id);
        return SchoolResponseDTO.of(s);
    }

    @Override
    public SchoolResponseDTO getByOwnerId() {
        School s = schoolService.findByOwnerIdWithCache();
        return SchoolResponseDTO.of(s);
    }

    @Override
    public School create(CreateSchoolRequestDTO dto) {
        return schoolService.create(dto);
    }

    @Override
    public School update(UUID id, UpdateSchoolRequestDTO dto) {
        return schoolService.update(id, dto);
    }
}
