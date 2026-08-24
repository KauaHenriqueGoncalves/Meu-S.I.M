package com.meusim.application.modules.identity.profile.schooladmin.scheduler;

import com.meusim.application.modules.identity.profile.schooladmin.SchoolAdmin;
import com.meusim.application.modules.identity.profile.schooladmin.repository.SchoolAdminRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class SchoolAdminSchedulerImpl implements  SchoolAdminScheduler {
    private static final Logger log = LoggerFactory.getLogger(SchoolAdminSchedulerImpl.class);
    private final SchoolAdminRepository repository;

    public SchoolAdminSchedulerImpl(SchoolAdminRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 */30 * * * *")
    public void deleteInactiveProfiles() {
        Instant limit = Instant.now().minus(Duration.ofMinutes(15));
        log.info("Iniciando job de remocao de perfis inativos. [limite={}]", limit);
        List<SchoolAdmin> inactiveAdmins = repository.findInactiveOlderThan(limit);
        if (inactiveAdmins.isEmpty()) {
            log.info("Nenhum perfil inativo encontrado para remocao.");
            return;
        }
        log.info("Perfis inativos encontrados para remocao. [total={}]", inactiveAdmins.size());
        inactiveAdmins.forEach(admin ->
                log.info("Removendo perfil inativo. [schoolAdminId={}] [userId={}] [schoolId={}] [createdAt={}]",
                        admin.getId(), admin.getUser().getId(), admin.getSchool().getId(), admin.getUser().getCreatedAt())
        );
        repository.deleteAll(inactiveAdmins);
        log.info("Job de remocao de perfis inativos concluido. [total={}]", inactiveAdmins.size());
    }
}
