package com.system.application.modules.identity.profile.schooladmin.service;

import com.system.application.modules.identity.profile.schooladmin.dto.UpdateSchoolAdminRequestDTO;
import com.system.application.modules.school.dto.CreateSchoolRequestDTO;
import com.system.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.system.application.modules.identity.base.user.dto.CreateUserRequestDTO;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface SchoolAdminService {
    Page<SchoolAdmin> pageBySchoolId(String name, int page, int size);
    SchoolAdmin findById(UUID id);
    SchoolAdmin findByUserId(UUID userId);
    SchoolAdmin create(CreateUserRequestDTO dto);
    SchoolAdmin createNewSchool(CreateUserRequestDTO createUserDto, CreateSchoolRequestDTO createSchoolDto);
    SchoolAdmin update(UUID id, UpdateSchoolAdminRequestDTO dto);
    void deleteById(UUID id);
}
