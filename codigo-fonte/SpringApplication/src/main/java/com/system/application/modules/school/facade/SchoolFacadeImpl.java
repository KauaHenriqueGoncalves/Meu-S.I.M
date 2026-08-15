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
    public School getByIdEntity(UUID id) {
        return schoolService.findById(id);
    }

    @Override
    public School getByIdEntityWithCache(UUID id) {
        return schoolService.findByIdWithCache(id);
    }

    @Override
    public SchoolResponseDTO getById(UUID id) {
        School school = schoolService.findByIdWithCache(id);
        return SchoolResponseDTO.of(school);
    }

    @Override
    public School getByUserIdEntity(UUID userId) {
        return schoolService.findByUserId(userId);
    }

    @Override
    public School getByUserIdEntityWithCache(UUID userId) {
        return schoolService.findByUserIdWithCache(userId);
    }

    @Override
    public School getByOwnerIdEntity() {
        return schoolService.findByOwnerId();
    }

    @Override
    public School getByOwnerIdEntityWithCache() {
        return schoolService.findByOwnerIdWithCache();
    }

    @Override
    public SchoolResponseDTO getByOwnerId() {
        School school = schoolService.findByOwnerIdWithCache();
        return SchoolResponseDTO.of(school);
    }

    @Override
    public School create(CreateSchoolRequestDTO dto) {
        return schoolService.create(dto);
    }

    @Override
    public School update(UUID id, UpdateSchoolRequestDTO dto) {
        return schoolService.update(id, dto);
    }

    @Override
    public void delete(UUID id) {
        // NOT IMPLEMENTED
    }
}
