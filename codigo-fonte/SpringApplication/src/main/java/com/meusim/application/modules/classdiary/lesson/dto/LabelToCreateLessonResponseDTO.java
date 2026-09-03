package com.meusim.application.modules.classdiary.lesson.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meusim.application.modules.classdiary.attendance.dto.CreateAttendanceRequestDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record LabelToCreateLessonResponseDTO(
        UUID scheduleId,
        UUID classroomId,
        LocalDate lessonDate,
        boolean isCanceled,
        String description,
        @JsonProperty("attendances")
        List<CreateAttendanceRequestDTO> attendancesDto
) { }
