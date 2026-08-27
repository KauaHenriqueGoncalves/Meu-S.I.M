package com.meusim.application.modules.classdiary.lesson.dto;

import com.meusim.application.modules.classdiary.lesson.Lesson;
import com.meusim.application.modules.classdiary.lesson.enums.LessonStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record LessonSimpleViewResponseDTO(
        UUID id,
        LocalDate lessonDate,
        LessonStatus status,
        int weekday,
        LocalTime startTime,
        LocalTime endTime
) {
    public static LessonSimpleViewResponseDTO of(Lesson l) {
        return new LessonSimpleViewResponseDTO(
                l.getId(),
                l.getLessonDate(),
                l.getStatus(),
                l.getWeekday(),
                l.getStartTime(),
                l.getEndTime()
        );
    }
}
