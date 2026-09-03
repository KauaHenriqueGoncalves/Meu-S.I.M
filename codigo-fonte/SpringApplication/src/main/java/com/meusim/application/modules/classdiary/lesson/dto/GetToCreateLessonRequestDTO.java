package com.meusim.application.modules.classdiary.lesson.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record GetToCreateLessonRequestDTO(
        @NotNull(message = "Informe a data da Agenda")
        UUID scheduleId,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate lessonDate
) { }
