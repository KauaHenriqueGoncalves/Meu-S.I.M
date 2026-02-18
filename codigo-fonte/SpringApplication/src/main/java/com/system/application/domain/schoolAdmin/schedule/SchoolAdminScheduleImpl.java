package com.system.application.domain.schoolAdmin.schedule;

import com.system.application.domain.schoolAdmin.SchoolAdmin;
import com.system.application.domain.schoolAdmin.repository.SchoolAdminRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class SchoolAdminScheduleImpl implements SchoolAdminSchedule{
    private final SchoolAdminRepository schoolAdminRepository;

    public SchoolAdminScheduleImpl(SchoolAdminRepository schoolAdminRepository) {
        this.schoolAdminRepository = schoolAdminRepository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 */30 * * * *")
    public void deleteInactiveProfiles() {
        // Usuarios inativos e que foram criados acima de 15 minutos
        // Esse é para usarios que acabaram de criar o perfil
        // Mas vai acabar afetando usuarios SchoolAdmin inativos na escola
        Instant limit = Instant.now().minus(Duration.ofMinutes(15));
        List<SchoolAdmin> inactiveAdmins = schoolAdminRepository.findInactiveOlderThan(limit);
        schoolAdminRepository.deleteAll(inactiveAdmins);
    }
}
