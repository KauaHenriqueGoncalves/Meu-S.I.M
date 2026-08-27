package com.meusim.application.modules.classdiary.lesson.facade;

import com.meusim.application.modules.classdiary.lesson.Lesson;
import com.meusim.application.modules.classdiary.lesson.dto.AgendaDayResponseDTO;
import com.meusim.application.modules.classdiary.lesson.dto.CreateLessonRequestDTO;
import com.meusim.application.modules.classdiary.lesson.dto.LessonDetailViewResponseDTO;
import com.meusim.application.modules.classdiary.lesson.dto.LessonSimpleViewResponseDTO;
import com.meusim.application.modules.classdiary.lesson.service.LessonService;
import com.meusim.application.shared.dto.PageResponse;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
public class LessonFacadeImpl implements LessonFacade {
    private final LessonService lessonService;

    public LessonFacadeImpl(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Override
    public PageResponse<LessonSimpleViewResponseDTO> page(UUID classroomId, int page, int size) {
        PageResponse<Lesson> pageResponse = lessonService.pageByClassroomIdWithCache(classroomId, page, size);
        return pageResponse.map(LessonSimpleViewResponseDTO::of);
    }

    @Override
    public List<AgendaDayResponseDTO> getAgendaByMonth(UUID classroomId, int year, int month) {
        return lessonService.findAgendaByMonthWithCache(classroomId, year, month);
    }

    @Override
    public LessonDetailViewResponseDTO getById(UUID lessonId) {
        Lesson lesson = lessonService.findByIdWithCache(lessonId);
        return LessonDetailViewResponseDTO.of(lesson);
    }

    @Override
    public Lesson create(CreateLessonRequestDTO dto) {
        return lessonService.create(dto);
    }
}
