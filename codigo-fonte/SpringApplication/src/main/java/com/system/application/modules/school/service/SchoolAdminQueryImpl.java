package com.system.application.modules.school.service;

import com.system.application.modules.identity.schooladmin.SchoolAdmin;
import com.system.application.modules.identity.schooladmin.repository.SchoolAdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SchoolAdminQueryImpl implements SchoolAdminQuery {
    private final static Logger log =
            LoggerFactory.getLogger(SchoolAdminQueryImpl.class);

    private final SchoolAdminRepository schoolAdminRepository;

    public SchoolAdminQueryImpl(SchoolAdminRepository schoolAdminRepository) {
        this.schoolAdminRepository = schoolAdminRepository;
    }

    @Override
    public List<SchoolAdmin> findAllBySchoolId(UUID schoolId) {
        if (schoolId == null) {
            log.error("schoolId is null. [schoolId={}]", schoolId);
            throw new IllegalArgumentException("schoolId cannot be null");
        }
        List<SchoolAdmin> admins = schoolAdminRepository.findAllBySchoolId(schoolId);
        log.info("Total de admins encontrado pelo Id do reforço. [size={}] [schoolId={}]",
                admins.size(), schoolId);
        return admins;
    }
}
