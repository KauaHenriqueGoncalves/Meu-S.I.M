package com.meusim.application.modules.classdiary.lesson.validator;

import com.meusim.application.modules.academic.classroom.Classroom;
import com.meusim.application.modules.academic.classschedule.ClassSchedule;
import com.meusim.application.modules.classdiary.lesson.repository.LessonRepository;
import com.meusim.application.modules.school.School;
import com.meusim.application.shared.exception.AccessDeniedException;
import com.meusim.application.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LessonValidatorImpl implements LessonValidator {
    private final static Logger log = LoggerFactory.getLogger(LessonValidatorImpl.class);
    private final LessonRepository repository;

    public LessonValidatorImpl(LessonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void ensureLessonClassroomBelongsSameSchool(School ownerSchool, Classroom classroom) {
        if (!classroom.getSchool().getId().equals(ownerSchool.getId())) {
            log.warn("A agenda não pertecem o mesmo reforco que o requisitante. [ownerSchoolId={}] [classroomSchoolId={}] [classroomId={}]",
                    ownerSchool.getId(), classroom.getSchool().getId(), classroom.getId());
            throw new AccessDeniedException("A agenda não pertence ao reforço");
        }
    }

    @Override
    public void ensureLessonClassroomBelongsSchedule(ClassSchedule schedule, Classroom classroom) {
        if (!schedule.getClassroom().getId().equals(classroom.getId())) {
            log.warn("A turma selecionada na Agenda não pertecente ao horário ou vice e versa. [scheduleId={}] [classroomId={}]",
                    schedule.getId(), classroom.getId());
            throw new AccessDeniedException("A turma não pertence a horário");
        }
    }

    @Override
    public void ensureLessonDateIsNotFuture(LocalDate lessonDate) {
        if (lessonDate.isAfter(LocalDate.now())) {
            log.warn("Tentativa de criar agenda com data futura. [lessonDate={}]", lessonDate);
            throw new BusinessException("Não é possível registrar uma agenda com data futura");
        }
    }

    @Override
    public void ensureLessonDateMatchesScheduleWeekday(ClassSchedule schedule, LocalDate lessonDate) {
        int expectedWeekday = schedule.getWeekday().getOrder();
        int actualWeekday = lessonDate.getDayOfWeek().getValue();
        if (expectedWeekday != actualWeekday) {
            log.warn("Data informada não corresponde ao dia da semana do horário. [lessonDate={}] [dayOfWeek={}] [expectedWeekday={}]",
                    lessonDate, actualWeekday, expectedWeekday);
            throw new BusinessException("A data informada não corresponde ao dia da semana do horário selecionado");
        }
    }

    @Override
    public void ensureDontExistsByScheduleIdAndLessonDate(ClassSchedule schedule, LocalDate lessonDate) {
        if (repository.existsByScheduleIdAndLessonDate(schedule.getId(), lessonDate)) {
            log.warn("Tentativa de duplicar agenda para o mesmo horário e data. [scheduleId={}] [lessonDate={}]",
                    schedule.getId(), lessonDate);
            throw new BusinessException("Já existe uma agenda registrada para este horário nesta data");
        }
    }
}
