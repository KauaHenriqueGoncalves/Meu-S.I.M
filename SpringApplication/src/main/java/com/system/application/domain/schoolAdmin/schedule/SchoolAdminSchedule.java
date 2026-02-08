package com.system.application.domain.schoolAdmin.schedule;

import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.schoolAdmin.repository.SchoolAdminRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchoolAdminSchedule {
    private final SchoolAdminRepository schoolAdminRepository;

    public SchoolAdminSchedule(SchoolAdminRepository schoolAdminRepository) {
        this.schoolAdminRepository = schoolAdminRepository;
    }

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void deleteInactiveProfiles() {
        List<SchoolAdmin> inactiveAdmins = schoolAdminRepository.findInactiveProfiles();
        schoolAdminRepository.deleteAll(inactiveAdmins);
    }
}
