package com.meusim.application.modules.school.scheduler;

import com.meusim.application.modules.school.School;
import com.meusim.application.modules.school.repository.SchoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class SchoolSchedulerImpl implements SchoolScheduler {
    private static final Logger log = LoggerFactory.getLogger(SchoolSchedulerImpl.class);
    private final SchoolRepository schoolRepository;

    public SchoolSchedulerImpl(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 5/30 * * * *") // 35 min
    public void deleteSchoolsWithoutUsers() {
        Instant start = Instant.now();
        Instant limit = Instant.now().minus(Duration.ofMinutes(15));
        log.info("Iniciando o job de delecao dos reforcos escolares sem usuarios. [start={}] [limit={}]",
                start, limit);
        List<School> abandoned = schoolRepository.findAbandonedSchools(limit);
        log.info("Total de reforcos escolares encotrado no job de delecao. [start={}] [size={}]",
                start, abandoned.size());
        schoolRepository.deleteAll(abandoned);
        Instant finish = Instant.now();
        log.info("finalizado o job de delecao de reforcos escolares sem usuarios. [start={}] [finish={}] [size={}]",
                start, finish, abandoned.size());
    }
}
