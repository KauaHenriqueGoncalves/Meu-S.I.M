package com.system.application.modules.academic.classschedule.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.modules.academic.classschedule.ClassSchedule;
import com.system.application.modules.academic.classschedule.dto.ClassScheduleRequest;
import com.system.application.modules.academic.classschedule.dto.ClassScheduleResponse;
import com.system.application.modules.academic.classschedule.repository.ClassScheduleRepository;
import com.system.application.modules.academic.classroom.Classroom;
import com.system.application.modules.academic.classroom.service.ClassroomService;
import com.system.application.modules.licensing.schoolsubscription.service.SchoolSubscriptionService;
import com.system.application.modules.school.School;
import com.system.application.modules.school.service.SchoolService;
import com.system.application.shared.exception.AccessDeniedException;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.exception.SubscriptionException;
import com.system.application.shared.services.cache.CacheService;
import com.system.application.shared.services.cache.keys.CacheKeys;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClassScheduleServiceImpl implements ClassScheduleService {
    private static final Logger log =
            LoggerFactory.getLogger(ClassScheduleServiceImpl.class);

    private final ClassScheduleRepository classScheduleRepository;
    private final SchoolSubscriptionService schoolSubscriptionService;
    private final SchoolService schoolService;
    private final ClassroomService classroomService;
    private final CacheService cacheService;

    private static final Duration CLASSSCHEDULE_TTL = Duration.ofHours(60);

    public ClassScheduleServiceImpl(
            ClassScheduleRepository classScheduleRepository,
            SchoolSubscriptionService schoolSubscriptionService,
            SchoolService schoolService,
            ClassroomService classroomService,
            CacheService cacheService
    ) {
        this.classScheduleRepository = classScheduleRepository;
        this.schoolSubscriptionService = schoolSubscriptionService;
        this.schoolService = schoolService;
        this.classroomService = classroomService;
        this.cacheService = cacheService;
    }

    @Override
    public List<ClassScheduleResponse> findAllResponseByClassroom(UUID userId, UUID classroomId) {
        School school = schoolService.findByUserId(userId);

        log.info("Buscando horários da turma. [requisitanteId={}] [classroomId={}] [schoolId={}]",
                userId, classroomId, school.getId());

        String key = CacheKeys.classSchedule(school.getId(), classroomId, "List");

        Optional<List<ClassScheduleResponse>> cacheResponse = cacheService.get(
                key,
                new TypeReference<>() {}
        );

        if (cacheResponse.isPresent()) {
            log.info("Horários da turma encontrados no cache. [classroomId={}] [total={}]",
                    classroomId, cacheResponse.get().size());
            return cacheResponse.get();
        }

        Classroom classroom = classroomService.findById(classroomId);
        ensureClassroomBelongsSchool(school.getId(), classroom);

        List<ClassSchedule> schedules = classScheduleRepository.findByClassroomId(classroom.getId()).get();

        List<ClassScheduleResponse> response = schedules.stream()
                .sorted(Comparator
                        .comparing((ClassSchedule c) -> c.getWeekday().getOrder())
                        .thenComparing(ClassSchedule::getStartTime)
                )
                .map(ClassScheduleResponse::of)
                .toList();

        log.info("Horários da turma encontrados. [classroomId={}] [total={}]",
                classroomId, response.size());

        cacheService.set(key, response, CLASSSCHEDULE_TTL);

        return response;
    }

    @Override
    public ClassSchedule findById(UUID classScheduleId) {
        log.info("Buscando Horario da classe pelo id. [classScheduleId={}]",
                classScheduleId);

        return classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> {
                    log.warn("Horário de turma não encontrado. [classScheduleId={}]", classScheduleId);
                    return new NotFoundObjectException("Não encontrou o horário da turma");
                });
    }

    @Override
    @Transactional
    public ClassSchedule save(UUID userId, UUID classroomId, ClassScheduleRequest request) {
        School school = schoolService.findByUserId(userId);

        log.info("Iniciando cadastro de horário de turma. [requisitanteId={}] [classroomId={}] [diaSemana={}] [inicio={}] [fim={}]",
                userId, classroomId, request.weekday(), request.startTime(), request.endTime());

        ensureSchoolHasActiveSubscription(school.getId());

        Classroom classroom = classroomService.findById(classroomId);
        ensureClassroomBelongsSchool(school.getId(), classroom);

        ClassSchedule classSchedule = new ClassSchedule(
                null,
                classroom,
                request.weekday(),
                request.startTime(),
                request.endTime()
        );
        classSchedule = classScheduleRepository.save(classSchedule);

        log.info("Horário de turma cadastrado com sucesso. [classScheduleId={}] [classroomId={}] [diaSemana={}] [inicio={}] [fim={}]",
                classSchedule.getId(), classroomId, request.weekday(), request.startTime(), request.endTime());

        String key = CacheKeys.classSchedulePattern(school.getId());

        log.info("Apagando todos os cache de horario de classe ligado à escola e classe. [school={}] [key={}]",
                school.getId(), key);

        cacheService.evictByPattern(key);

        return classSchedule;
    }

    @Override
    @Transactional
    public void update(UUID userId, UUID classroomId, UUID classScheduleId, ClassScheduleRequest updateRequest) {
        log.info("Iniciando atualização de horário de turma. [requisitanteId={}] [classScheduleId={}] [classroomId={}]",
                userId, classScheduleId, classroomId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        ClassSchedule schedule = findById(classScheduleId);
        ensureClassScheduleBelongsSchool(school.getId(), schedule);
        ensureClassScheduleBelongsClassroom(classroomId, schedule);

        schedule.setWeekday(updateRequest.weekday());
        schedule.setStartTime(updateRequest.startTime());
        schedule.setEndTime(updateRequest.endTime());

        log.info("Horário de turma atualizado com sucesso. [classScheduleId={}] [classroomId={}] [diaSemana={}] [inicio={}] [fim={}]",
                classScheduleId, classroomId, updateRequest.weekday(), updateRequest.startTime(), updateRequest.endTime());

        String key = CacheKeys.classSchedulePattern(school.getId());

        log.info("Apagando todos os cache de horario de classe ligado à escola e classe. [school={}] [key={}]",
                school.getId(), key);

        cacheService.evictByPattern(key);
    }

    @Override
    @Transactional
    public void deleteById(UUID userId, UUID classroomId, UUID classScheduleId) {
        log.info("Iniciando exclusão de horário de turma. [requisitanteId={}] [classScheduleId={}] [classroomId={}]",
                userId, classScheduleId, classroomId);

        School school = schoolService.findByUserId(userId);
        ensureSchoolHasActiveSubscription(school.getId());

        ClassSchedule schedule = findById(classScheduleId);
        ensureClassScheduleBelongsSchool(school.getId(), schedule);
        ensureClassScheduleBelongsClassroom(classroomId, schedule);

        classScheduleRepository.deleteById(schedule.getId());

        log.info("Horário de turma excluído com sucesso. [classScheduleId={}] [classroomId={}] [schoolId={}]",
                classScheduleId, classroomId, school.getId());

        String key = CacheKeys.classSchedulePattern(school.getId());

        log.info("Apagando todos os cache de horario de classe ligado à escola e classe. [school={}] [key={}]",
                school.getId(), key);

        cacheService.evictByPattern(key);
    }

    private void ensureClassroomBelongsSchool(UUID schoolId, Classroom classroom) {
        if (!classroom.getSchool().getId().equals(schoolId)) {
            log.warn("Tentativa de acesso a turma de outra escola. [classroomId={}] [classroomSchoolId={}] [schoolId={}]",
                    classroom.getId(), classroom.getSchool().getId(), schoolId);
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
    }

    private void ensureClassScheduleBelongsSchool(UUID schoolId, ClassSchedule classSchedule) {
        if (!classSchedule.getClassroom().getSchool().getId().equals(schoolId)) {
            log.warn("Tentativa de acesso a horário de turma de outra escola. [classScheduleId={}] [classroomSchoolId={}] [schoolId={}]",
                    classSchedule.getId(), classSchedule.getClassroom().getSchool().getId(), schoolId);
            throw new AccessDeniedException("Não é possivel interagir com turma de outra escola");
        }
    }

    private void ensureClassScheduleBelongsClassroom(UUID classroomId, ClassSchedule classSchedule) {
        if (!classSchedule.getClassroom().getId().equals(classroomId)) {
            log.warn("Tentativa de acesso a horário que não pertence à turma. [classScheduleId={}] [classScheduleClassroomId={}] [classroomId={}]",
                    classSchedule.getId(), classSchedule.getClassroom().getId(), classroomId);
            throw new AccessDeniedException("Não é possivel interagir com o horário de outra turma");
        }
    }

    private void ensureSchoolHasActiveSubscription(UUID schoolId) {
        try {
            schoolSubscriptionService.findActiveSubscriptionBySchoolId(schoolId);
        }
        catch (SubscriptionException e) {
            log.warn("Operação bloqueada: escola sem licença ativa. [schoolId={}]", schoolId);
            throw new SubscriptionException("A escola não possui licença ativa.");
        }
    }
}
