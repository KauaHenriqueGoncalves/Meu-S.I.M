package com.system.application.modules.school.service;

import com.system.application.modules.identity.schooladmin.SchoolAdmin;

import java.util.List;
import java.util.UUID;

public interface SchoolAdminQuery {
    List<SchoolAdmin> findAllBySchoolId(UUID schoolId);
}
