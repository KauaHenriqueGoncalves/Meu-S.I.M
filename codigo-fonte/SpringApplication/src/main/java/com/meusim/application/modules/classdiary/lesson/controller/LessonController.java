package com.meusim.application.modules.classdiary.lesson.controller;

import com.meusim.application.modules.classdiary.lesson.dto.AgendaDayResponseDTO;
import com.meusim.application.modules.classdiary.lesson.dto.CreateLessonRequestDTO;
import com.meusim.application.modules.classdiary.lesson.dto.LessonDetailViewResponseDTO;
import com.meusim.application.modules.classdiary.lesson.dto.LessonSimpleViewResponseDTO;
import com.meusim.application.modules.classdiary.lesson.facade.LessonFacade;
import com.meusim.application.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lessons")
public class LessonController {
    private final LessonFacade facade;

    public LessonController(LessonFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/{classroomId}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<PageResponse<LessonSimpleViewResponseDTO>> getPage(@PathVariable UUID classroomId,
                                                                             @RequestParam int page,
                                                                             @RequestParam int size) {
        return ResponseEntity.ok(facade.page(classroomId, page, size));
    }

    @GetMapping("/{classroomId}/{lessonId}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<LessonDetailViewResponseDTO> getDetail(@PathVariable UUID lessonId) {
        return ResponseEntity.ok(facade.getById(lessonId));
    }

    @GetMapping("/{classroomId}/agenda")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<List<AgendaDayResponseDTO>> getAgendaDays(@PathVariable UUID classroomId,
                                                                    @RequestParam int year,
                                                                    @RequestParam int month) {
        List<AgendaDayResponseDTO> agendas = facade.getAgendaByMonth(classroomId, year, month);
        return ResponseEntity.ok(agendas);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> create(@RequestBody @Valid CreateLessonRequestDTO dto) {
        facade.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
