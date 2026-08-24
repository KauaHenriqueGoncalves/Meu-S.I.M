package com.meusim.application.modules.identity.profile.schooladmin.facade;

import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.meusim.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.meusim.application.modules.identity.profile.schooladmin.dto.SchoolAdminDetailViewResponseDTO;
import com.meusim.application.modules.identity.profile.schooladmin.dto.SchoolAdminSimpleViewResponseDTO;
import com.meusim.application.modules.identity.profile.schooladmin.dto.UpdateSchoolAdminRequestDTO;
import com.meusim.application.modules.identity.profile.schooladmin.service.SchoolAdminService;
import com.meusim.application.modules.school.dto.CreateSchoolRequestDTO;
import com.meusim.application.shared.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SchoolAdminFacadeImpl implements SchoolAdminFacade {
    private final SchoolAdminService service;

    public SchoolAdminFacadeImpl(SchoolAdminService service) {
        this.service = service;
    }

    @Override
    public SchoolAdmin getEntityById(UUID id) {
        return service.findById(id);
    }

    @Override
    public PageResponse<SchoolAdminSimpleViewResponseDTO> pageBySchool(String name, int page, int size) {
        Page<SchoolAdmin> pageAdmin = service.pageBySchoolWithCache(name, page, size);
        Page<SchoolAdminSimpleViewResponseDTO> simpleView = pageAdmin.map(SchoolAdminSimpleViewResponseDTO::of);
        return PageResponse.from(simpleView);
    }

    @Override
    public SchoolAdminDetailViewResponseDTO getById(UUID id) {
        SchoolAdmin sa = service.findByIdWithCache(id);
        return SchoolAdminDetailViewResponseDTO.of(sa);
    }

    @Override
    public SchoolAdmin create(CreateUserRequestDTO dto) {
        return service.create(dto);
    }

    @Override
    public SchoolAdmin createNewSchool(CreateUserRequestDTO createUserDto, CreateSchoolRequestDTO createSchoolDto) {
        return service.createNewSchool(createUserDto, createSchoolDto);
    }

    @Override
    public SchoolAdmin update(UUID id, UpdateSchoolAdminRequestDTO dto) {
        return service.update(id, dto);
    }

    @Override
    public void delete(UUID id) {
        service.deleteById(id);
    }
}
