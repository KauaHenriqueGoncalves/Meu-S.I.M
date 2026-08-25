package com.meusim.application.modules.classdiary.lesson.service;

import com.meusim.application.auth.service.AuthenticatedUserService;
import com.meusim.application.modules.academic.classroom.Classroom;
import com.meusim.application.modules.academic.classroom.service.ClassroomService;
import com.meusim.application.modules.academic.classschedule.ClassSchedule;
import com.meusim.application.modules.academic.classschedule.service.ClassScheduleService;
import com.meusim.application.modules.classdiary.lesson.Lesson;
import com.meusim.application.modules.classdiary.lesson.dto.AgendaDayResponseDTO;
import com.meusim.application.modules.classdiary.lesson.dto.CreateLessonRequestDTO;
import com.meusim.application.modules.classdiary.lesson.repository.LessonRepository;
import com.meusim.application.modules.classdiary.lesson.validator.LessonValidator;
import com.meusim.application.modules.identity.base.user.dto.ResponsibleSnapshotDTO;
import com.meusim.application.modules.identity.base.user.facade.UserFacade;
import com.meusim.application.modules.school.School;
import com.meusim.application.modules.school.facade.SchoolFacade;
import com.meusim.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

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
        ResponsibleSnapshotDTO responsibleSnapshot = userFacade.getResponsible(ownerId);
        Classroom classroom = classroomService.findById(dto.classroomId());
        ClassSchedule schedule = scheduleService.findById(dto.scheduleId());

        if (dto.isCanceled()) {
            // TODO: não fazer a persistencia do restante, apenas desses dados com a descrition
        }

        // TODO fazer a persistencia

        return null;
    }

    @Override
    public List<AgendaDayResponseDTO> findAgendaByMonth(UUID classroomId, int year, int month) {
        return List.of();
    }
}
