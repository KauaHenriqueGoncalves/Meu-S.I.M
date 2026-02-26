package com.system.application.domain.school.schedule;

import com.system.application.domain.school.School;
import com.system.application.domain.school.repository.SchoolRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class SchoolSchedule {
    private final SchoolRepository schoolRepository;

    public SchoolSchedule(
            SchoolRepository schoolRepository
    ) {
        this.schoolRepository = schoolRepository;
    }

    @Transactional
    @Scheduled(cron = "0 5/30 * * * *")
    public void deleteSchoolsWithoutUsers() {
        // O Schedule é agendado para rodar no minuto 5 apos 30 minutos, para evitar "concorrencia" com o schedule
        // do AdminSchool
        Instant limit = Instant.now().minus(Duration.ofMinutes(15));
        List<School> abandoned = schoolRepository.findAbandonedSchools(limit);
        schoolRepository.deleteAll(abandoned);
    }
}
