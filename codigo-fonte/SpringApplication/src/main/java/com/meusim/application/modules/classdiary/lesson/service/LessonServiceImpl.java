package com.meusim.application.modules.classdiary.lesson.service;

import com.meusim.application.auth.service.AuthenticatedUserService;
import com.meusim.application.modules.academic.classroom.Classroom;
import com.meusim.application.modules.academic.classroom.service.ClassroomService;
import com.meusim.application.modules.academic.classschedule.ClassSchedule;
import com.meusim.application.modules.academic.classschedule.service.ClassScheduleService;
import com.meusim.application.modules.classdiary.lesson.Lesson;
import com.meusim.application.modules.classdiary.lesson.dto.AgendaDayResponseDTO;
import com.meusim.application.modules.classdiary.lesson.dto.CreateLessonRequestDTO;
import com.meusim.application.modules.classdiary.lesson.enums.LessonDisplayStatus;
import com.meusim.application.modules.classdiary.lesson.enums.LessonStatus;
import com.meusim.application.modules.classdiary.lesson.repository.LessonRepository;
import com.meusim.application.modules.classdiary.lesson.validator.LessonValidator;
import com.meusim.application.modules.identity.base.user.dto.ResponsibleSnapshotDTO;
import com.meusim.application.modules.identity.base.user.facade.UserFacade;
import com.meusim.application.modules.school.School;
import com.meusim.application.modules.school.facade.SchoolFacade;
import com.meusim.application.shared.exception.BusinessException;
import com.meusim.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LessonServiceImpl implements LessonService {
    private static final Logger log = LoggerFactory.getLogger(LessonServiceImpl.class);
    private final AuthenticatedUserService authenticatedUserService;
    private final LessonRepository lessonRepository;
    private final LessonValidator validator;
    private final SchoolFacade schoolFacade;
    private final UserFacade userFacade;
    private final ClassroomService classroomService;
    private final ClassScheduleService scheduleService;

    public LessonServiceImpl(AuthenticatedUserService authenticatedUserService,
                             LessonRepository lessonRepository,
                             LessonValidator validator,
                             SchoolFacade schoolFacade,
                             UserFacade userFacade,
                             ClassroomService classroomService,
                             ClassScheduleService scheduleService) {
        this.authenticatedUserService = authenticatedUserService;
        this.lessonRepository = lessonRepository;
        this.validator = validator;
        this.schoolFacade = schoolFacade;
        this.userFacade = userFacade;
        this.classroomService = classroomService;
        this.scheduleService = scheduleService;
    }


    @Override
    public Page<Lesson> pageByClassroomId(UUID classroomId, int page, int size) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        School ownerSchool = schoolFacade.getEntityByOwnerIdWithCache();
        Classroom classroom = classroomService.findById(classroomId);
        log.info("Buscando página de agenda da turma pelo ID. [ownerId={}] [classroomId={}] [ownerSchoolId={}] [page={}] [size={}]",
                ownerId, classroom.getId(), ownerSchool.getId(), page, size);
        validator.ensureLessonClassroomBelongsSameSchool(ownerSchool, classroom);
        Pageable sortedPageable = PageRequest.of(page, size, Sort.by("lesson_date").ascending());
        Page<Lesson> lessonPage = lessonRepository.findAllByClassroomId(classroomId, sortedPageable);
        log.info("Pagina da agenda da turma pelo ID retornada com sucesso. [ownerId={}] [classroomId={}] [ownerSchoolId={}] [page={}] [size={}]",
                ownerId, classroom.getId(), ownerSchool.getId(), page, size);
        return lessonPage;
    }

    @Override
    public List<AgendaDayResponseDTO> findAgendaByMonth(UUID classroomId, int year, int month) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        School ownerSchool = schoolFacade.getEntityByOwnerIdWithCache();
        Classroom classroom = classroomService.findById(classroomId);
        validator.ensureLessonClassroomBelongsSameSchool(ownerSchool, classroom);
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<ClassSchedule> schedules = scheduleService.findAllByClassroomId(classroomId);
        List<Lesson> existingLessons = lessonRepository.findByClassroomIdAndLessonDateBetween(classroomId, startDate, endDate);
        Map<String, Lesson> lessonByKey = existingLessons.stream()
                .collect(Collectors.toMap(
                        l -> l.getScheduleId() + "_" + l.getLessonDate(), // key
                        l -> l // value
                ));
        List<AgendaDayResponseDTO> agendas = new ArrayList<>();
        for (ClassSchedule schedule : schedules) {
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                if (date.getDayOfWeek().getValue() != schedule.getWeekday().getOrder()) {
                    continue;
                }
                String key = schedule.getId() + "_" + date;
                Lesson lesson = lessonByKey.get(key);
                if (date.getYear() != LocalDate.now().getYear() && lesson == null) {
                    continue;
                }
                LessonDisplayStatus displayStatus;
                if (lesson != null) {
                    displayStatus = lesson.getStatus() == LessonStatus.DONE
                            ? LessonDisplayStatus.DONE
                            : LessonDisplayStatus.CANCELED;
                } else {
                    displayStatus = date.isBefore(LocalDate.now())
                            ? LessonDisplayStatus.LATE
                            : LessonDisplayStatus.PENDING;
                }
                agendas.add(new AgendaDayResponseDTO(
                        lesson != null ? lesson.getId() : null,
                        schedule.getId(),
                        date,
                        schedule.getWeekday().getDescription(),
                        schedule.getStartTime(),
                        schedule.getEndTime(),
                        displayStatus,
                        lesson != null ? lesson.getDescription() : null
                ));
            }
        }
        agendas.sort(Comparator.comparing(AgendaDayResponseDTO::date)
                .thenComparing(AgendaDayResponseDTO::startTime));
        return agendas;
    }

    @Override
    public Lesson findById(UUID id) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Buscando agenda do reforco pelo ID no banco. [ownerId={}] [lessonId={}]",
                ownerId, id);
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("A agenda do reforco não foi econtrado pelo Id no banco. [ownerId={}] [lessonId={}]",
                            ownerId, id);
                    return new NotFoundObjectException("Agenda não foi encontrada");
                });
        log.info("Agenda encontrada com sucesso pelo ID do banco. [ownerId={}] [lessonId={}]",
                ownerId, id);
        return lesson;
    }

    @Override
    @Transactional
    public Lesson create(CreateLessonRequestDTO dto) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        School school = schoolFacade.getEntityByOwnerIdWithCache();
        log.info("Criado uma nova Agenda da turma. [ownerId={}] [schoolId={}] [classroomId={}] [scheduleId={}]",
                ownerId, school.getId(), dto.classroomId(), dto.scheduleId());
        ResponsibleSnapshotDTO responsibleSnapshot = userFacade.getResponsible(ownerId);
        Classroom classroom = classroomService.findById(dto.classroomId());
        validator.ensureLessonClassroomBelongsSameSchool(school, classroom);
        ClassSchedule schedule = scheduleService.findById(dto.scheduleId());
        validator.ensureLessonClassroomBelongsSchedule(schedule, classroom);
        LocalDate lessonDate = (dto.lessonDate() != null) ? dto.lessonDate() : LocalDate.now();
        validator.ensureLessonDateIsNotFuture(lessonDate);
        validator.ensureLessonDateMatchesScheduleWeekday(schedule, lessonDate);
        validator.ensureDontExistsByScheduleIdAndLessonDate(schedule, lessonDate);
        if (lessonDate.getYear() != LocalDate.now().getYear()) {
            log.warn("Tentativa de criar uma agenda não correspondete a data atual. [ownerId={}] [schoolId={}] [classroomId={}] [scheduleId={}]",
                    ownerId, school.getId(), dto.classroomId(), dto.scheduleId());
            throw new BusinessException("A data informada não corresponde ao ano atual.");
        }
        Lesson lesson = new Lesson();
        lesson.setClassroom(classroom);
        lesson.setLessonDate(lessonDate);
        lesson.setStatus(dto.isCanceled() ? LessonStatus.CANCELED : LessonStatus.DONE);
        lesson.setResponsibleId(responsibleSnapshot.responsibleId());
        lesson.setResponsibleUsername(responsibleSnapshot.username());
        lesson.setResponsibleRole(responsibleSnapshot.role());
        lesson.setClassroomName(classroom.getName());
        lesson.setSubjectName(classroom.getSubject().getName());
        lesson.setScheduleId(schedule.getId());
        lesson.setWeekday(schedule.getWeekday().getOrder());
        lesson.setStartTime(schedule.getStartTime());
        lesson.setEndTime(schedule.getEndTime());
        lesson.setDescription(dto.description());
        lesson = lessonRepository.save(lesson);
        log.info("Agenda criada. [ownerId={}] [schoolId={}] [lessonId={}] [status={}]",
                ownerId, school.getId(), lesson.getId(), lesson.getStatus());
        return lesson;
    }
}
