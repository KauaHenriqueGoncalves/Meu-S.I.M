package com.meusim.application.modules.classdiary.lesson.facade;

import com.meusim.application.modules.classdiary.lesson.Lesson;
import com.meusim.application.modules.classdiary.lesson.dto.AgendaDayResponseDTO;
import com.meusim.application.modules.classdiary.lesson.dto.CreateLessonRequestDTO;
import com.meusim.application.modules.classdiary.lesson.dto.LessonDetailViewResponseDTO;
import com.meusim.application.modules.classdiary.lesson.dto.LessonSimpleViewResponseDTO;
import com.meusim.application.shared.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface LessonFacade {
    PageResponse<LessonSimpleViewResponseDTO> page(UUID classroomId, int page, int size);
    List<AgendaDayResponseDTO> getAgendaByMonth(UUID classroomId, int year, int month);
    LessonDetailViewResponseDTO getById(UUID lessonId);
    Lesson create(CreateLessonRequestDTO dto);
}
