package com.system.application.modules.identity.schooladmin.scheduler;

import com.system.application.modules.identity.schooladmin.SchoolAdmin;
import com.system.application.modules.identity.schooladmin.repository.SchoolAdminRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class SchoolAdminScheduler {
    private static final Logger log =
            LoggerFactory.getLogger(SchoolAdminScheduler.class);

    private final SchoolAdminRepository schoolAdminRepository;

    public SchoolAdminScheduler(
            SchoolAdminRepository schoolAdminRepository
    ) {
        this.schoolAdminRepository = schoolAdminRepository;
    }

    @Transactional
    @Scheduled(cron = "0 */30 * * * *")
    public void deleteInactiveProfiles() {
        Instant limit = Instant.now().minus(Duration.ofMinutes(15));

        log.info("Iniciando job de remocao de perfis inativos. [limite={}]", limit);

        List<SchoolAdmin> inactiveAdmins = schoolAdminRepository.findInactiveOlderThan(limit);

        if (inactiveAdmins.isEmpty()) {
            log.info("Nenhum perfil inativo encontrado para remocao.");
            return;
        }

        log.info("Perfis inativos encontrados para remocao. [total={}]", inactiveAdmins.size());

        inactiveAdmins.forEach(admin ->
                log.info("Removendo perfil inativo. [schoolAdminId={}] [userId={}] [schoolId={}] [createdAt={}]",
                        admin.getId(), admin.getUser().getId(), admin.getSchool().getId(), admin.getUser().getCreatedAt())
        );

        schoolAdminRepository.deleteAll(inactiveAdmins);

        log.info("Job de remocao de perfis inativos concluido. [total={}]", inactiveAdmins.size());
    }
}
