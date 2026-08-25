package com.meusim.application.modules.classdiary.lesson.service;

import com.meusim.application.modules.classdiary.lesson.Lesson;
import com.meusim.application.modules.classdiary.lesson.dto.AgendaDayResponseDTO;
import com.meusim.application.modules.classdiary.lesson.dto.CreateLessonRequestDTO;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface LessonService {
    Page<Lesson> pageByClassroomId(UUID classroomId, int page, int size);
    Lesson findById(UUID id);
    Lesson create(CreateLessonRequestDTO dto);
    List<AgendaDayResponseDTO> findAgendaByMonth(UUID classroomId, int year, int month);
}
