package com.system.application.modules.school.query;

import com.system.application.modules.identity.profile.schooladmin.SchoolAdmin;
import java.util.List;
import java.util.UUID;

public interface SchoolAdminQuery {
    List<SchoolAdmin> findAllBySchoolId(UUID schoolId);
}
