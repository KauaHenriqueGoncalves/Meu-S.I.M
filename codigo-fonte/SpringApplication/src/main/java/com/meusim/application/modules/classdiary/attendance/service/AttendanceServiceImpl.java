package com.meusim.application.modules.classdiary.attendance.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meusim.application.auth.service.AuthenticatedUserService;
import com.meusim.application.modules.classdiary.attendance.Attendance;
import com.meusim.application.modules.classdiary.attendance.cache.AttendanceCacheKeys;
import com.meusim.application.modules.classdiary.attendance.dto.CreateAttendanceRequestDTO;
import com.meusim.application.modules.classdiary.attendance.repository.AttendanceRepository;
import com.meusim.application.modules.classdiary.lesson.Lesson;
import com.meusim.application.shared.exception.BusinessException;
import com.meusim.application.shared.services.cache.CacheService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);
    private final AuthenticatedUserService authenticatedUserService;
    private final AttendanceRepository attendanceRepository;
    private final CacheService cacheService;

    public AttendanceServiceImpl(AuthenticatedUserService authenticatedUserService,
                                 AttendanceRepository attendanceRepository,
                                 CacheService cacheService) {
        this.authenticatedUserService = authenticatedUserService;
        this.attendanceRepository = attendanceRepository;
        this.cacheService = cacheService;
    }

    @Override
    public List<Attendance> findAllByLessonId(UUID lessonId) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Buscando todas as presencas de agenda. [ownerId={}] [lessonId={}]", ownerId, lessonId);
        List<Attendance> attendances = findAllByLessonIdWithCache(lessonId);
        log.info("Total de presencas encontradas. [ownerId={}] [size={}]", ownerId, attendances.size());
        return attendances;
    }

    @Override
    public List<Attendance> findAllByLessonIdWithCache(UUID lessonId) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        String key = AttendanceCacheKeys.byLessonId(lessonId);
        Optional<List<Attendance>> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Presencas encontradas no cache. [ownerId={}] [lessonId={}] [size={}]",
                    ownerId, lessonId, cache.get().size());
            return cache.get();
        }

        List<Attendance> attendances = attendanceRepository.findAllByLessonId(lessonId);
        log.info("Armazenando os dados de attendances. [ownerId={}] [total={}] [key={}]",
                ownerId, attendances.size(), key);
        cacheService.set(key, attendances, AttendanceCacheKeys.TTL);
        return attendances;
    }

    @Override
    @Transactional
    public List<Attendance> createAll(Lesson lessonEntity, List<CreateAttendanceRequestDTO> list) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Criando as presencas da agenda. [ownerId={}] [lessonId={}] [totalAttendance={}]",
                ownerId, lessonEntity.getId(), list.size());
        if (list.isEmpty()) {
            log.warn("Tentativa de criar agenda sem nenhuma presenca informada. [ownerId={}] [lessonId={}]",
                    ownerId, lessonEntity.getId());
            throw new BusinessException("É necessário informar ao menos uma presença");
        }
        List<Attendance> attendancesToCreate = list.stream()
                .map(dto -> Attendance.createInit(lessonEntity, dto))
                .toList();
        List<Attendance> attendances = attendanceRepository.saveAll(attendancesToCreate);
        log.info("Attendaces criados com sucesso! [ownerId={}] [size={}]", ownerId, attendances.size());
        return attendances;
    }
}
