package com.meusim.application.modules.identity.profile.schooladmin.service;

import com.meusim.application.modules.identity.profile.schooladmin.dto.UpdateSchoolAdminRequestDTO;
import com.meusim.application.modules.school.dto.CreateSchoolRequestDTO;
import com.meusim.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.meusim.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import com.meusim.application.shared.dto.PageResponse;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface SchoolAdminService {
    Page<SchoolAdmin> pageBySchool(String name, int page, int size);
    PageResponse<SchoolAdmin> pageBySchoolWithCache(String name, int page, int size);
    SchoolAdmin findById(UUID id);
    SchoolAdmin findByIdWithCache(UUID id);
    SchoolAdmin findByUserId(UUID userId);
    SchoolAdmin findByUserIdWithCache(UUID userId);
    SchoolAdmin create(CreateUserRequestDTO dto);
    SchoolAdmin createNewSchool(CreateUserRequestDTO createUserDto, CreateSchoolRequestDTO createSchoolDto);
    SchoolAdmin update(UUID id, UpdateSchoolAdminRequestDTO dto);
    void deleteById(UUID id);
}
