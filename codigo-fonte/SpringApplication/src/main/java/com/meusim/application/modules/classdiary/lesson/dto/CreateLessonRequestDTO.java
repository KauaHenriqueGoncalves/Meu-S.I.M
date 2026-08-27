package com.meusim.application.modules.classdiary.lesson.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meusim.application.shared.validation.NoEmoji;
import com.meusim.application.shared.validation.NoLeadingTrailingSpace;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record CreateLessonRequestDTO(
        @NotNull(message = "Informe a data da Agenda")
        UUID scheduleId,

        @NotNull(message = "Informe a classe")
        UUID classroomId,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate lessonDate,

        @NotNull(message = "Informe se a Agenda foi cancelada")
        boolean isCanceled,

        @NotNull(message = "A descrição da Agenda não pode ser nula")
        @Size(max = 500, message = "Descrição da Agenda até 500 caracteres")
        @NoLeadingTrailingSpace
        @NoEmoji(message = "Não é permitido o recebimento de emoji")
        String description
) { }
