package com.meusim.application.modules.classdiary.lesson.dto;

import com.meusim.application.modules.classdiary.lesson.Lesson;
import com.meusim.application.modules.classdiary.lesson.enums.LessonStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record LessonDetailViewResponseDTO(
        UUID id,
        LocalDate lessonDate,
        LessonStatus status,
        UUID responsibleId, // <- collaboratorId || schoolAdminId
        String responsibleUsername,
        String responsibleRole,
        UUID classroomId,
        String classroomName,
        String subjectName,
        UUID scheduleId,
        int weekday,
        LocalTime startTime,
        LocalTime endTime,
        String description
) {
    public static LessonDetailViewResponseDTO of(Lesson l) {
        return new LessonDetailViewResponseDTO(
                l.getId(),
                l.getLessonDate(),
                l.getStatus(),
                l.getResponsibleId(),
                l.getResponsibleUsername(),
                l.getResponsibleRole(),
                l.getClassroom().getId(),
                l.getClassroomName(),
                l.getSubjectName(),
                l.getScheduleId(),
                l.getWeekday(),
                l.getStartTime(),
                l.getEndTime(),
                l.getDescription()
        );
    }
}
