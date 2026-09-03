package com.meusim.application.modules.classdiary.lesson.facade;

import com.meusim.application.modules.classdiary.lesson.Lesson;
import com.meusim.application.modules.classdiary.lesson.dto.*;
import com.meusim.application.shared.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface LessonFacade {
    PageResponse<LessonSimpleViewResponseDTO> page(UUID classroomId, int page, int size);
    List<AgendaDayResponseDTO> getAgendaByMonth(UUID classroomId, int year, int month);
    LabelToCreateLessonResponseDTO getLabelToCreateLesson(UUID classroomId, GetToCreateLessonRequestDTO dto);
    LessonDetailViewResponseDTO getById(UUID lessonId);
    Lesson create(CreateLessonRequestDTO dto);
}
