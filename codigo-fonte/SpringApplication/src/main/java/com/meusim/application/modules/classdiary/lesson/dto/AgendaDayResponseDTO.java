package com.meusim.application.modules.classdiary.lesson.dto;

import com.meusim.application.modules.classdiary.lesson.enums.LessonDisplayStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AgendaDayResponseDTO(
        UUID lessonId,          // null se não foi gerado ainda
        UUID scheduleId,
        LocalDate date,
        String weekday,
        LocalTime startTime,
        LocalTime endTime,
        LessonDisplayStatus displayStatus,
        String content                     // null se não existe lesson ainda
) { }
