package com.meusim.application.modules.identity.profile.schooladmin.facade;

import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.meusim.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.meusim.application.modules.identity.profile.schooladmin.dto.SchoolAdminDetailViewResponseDTO;
import com.meusim.application.modules.identity.profile.schooladmin.dto.SchoolAdminSimpleViewResponseDTO;
import com.meusim.application.modules.identity.profile.schooladmin.dto.UpdateSchoolAdminRequestDTO;
import com.meusim.application.modules.school.dto.CreateSchoolRequestDTO;
import com.meusim.application.shared.dto.PageResponse;
import java.util.UUID;

public interface SchoolAdminFacade {
    SchoolAdmin getEntityById(UUID id);
    PageResponse<SchoolAdminSimpleViewResponseDTO> pageBySchool(String name, int page, int size);
    SchoolAdminDetailViewResponseDTO getById(UUID id);
    SchoolAdmin create(CreateUserRequestDTO dto);
    SchoolAdmin createNewSchool(CreateUserRequestDTO createUserDto, CreateSchoolRequestDTO createSchoolDto);
    SchoolAdmin update(UUID id, UpdateSchoolAdminRequestDTO dto);
    void delete(UUID id);
}
