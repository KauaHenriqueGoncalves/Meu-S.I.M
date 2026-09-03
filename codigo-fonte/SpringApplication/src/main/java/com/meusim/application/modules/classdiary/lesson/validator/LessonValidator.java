package com.meusim.application.modules.classdiary.lesson.validator;

import com.meusim.application.modules.academic.classroom.Classroom;
import com.meusim.application.modules.academic.classschedule.ClassSchedule;
import com.meusim.application.modules.school.School;

import java.time.LocalDate;

public interface LessonValidator {
    void ensureLessonClassroomBelongsSameSchool(School ownerSchool, Classroom classroom);
    void ensureLessonClassroomBelongsSchedule(ClassSchedule schedule, Classroom classroom);
    void ensureLessonDateIsNotFuture(LocalDate lessonDate);
    void ensureLessonDateMatchesScheduleWeekday(ClassSchedule schedule, LocalDate lessonDate);
    void ensureDontExistsByScheduleIdAndLessonDate(ClassSchedule schedule, LocalDate lessonDate);
    void ensureSchoolHasSubscription(School school);
}
