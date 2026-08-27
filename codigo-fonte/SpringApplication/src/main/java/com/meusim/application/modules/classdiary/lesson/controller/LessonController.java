package com.meusim.application.modules.classdiary.lesson.controller;

import com.meusim.application.modules.classdiary.lesson.dto.AgendaDayResponseDTO;
import com.meusim.application.modules.classdiary.lesson.dto.CreateLessonRequestDTO;
import com.meusim.application.modules.classdiary.lesson.service.LessonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lesson")
public class LessonController {
    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/{classroomId}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<List<AgendaDayResponseDTO>> getAgendaDays(@RequestParam int year,
                                                                    @RequestParam int month,
                                                                    @PathVariable UUID classroomId) {
        List<AgendaDayResponseDTO> agendas = lessonService.findAgendaByMonth(classroomId, year, month);
        return ResponseEntity.ok(agendas);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> create(@RequestBody @Valid CreateLessonRequestDTO dto) {
        lessonService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
