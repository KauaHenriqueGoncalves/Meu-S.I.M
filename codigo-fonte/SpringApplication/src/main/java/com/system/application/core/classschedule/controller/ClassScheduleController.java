package com.system.application.core.classschedule.controller;

import com.system.application.core.classschedule.ClassSchedule;
import com.system.application.core.classschedule.dto.ClassScheduleRequest;
import com.system.application.core.classschedule.dto.ClassScheduleResponse;
import com.system.application.core.classschedule.service.ClassScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classroom/{classroomId}/schedule")
public class ClassScheduleController {
    private final ClassScheduleService classScheduleService;

    public ClassScheduleController(
            ClassScheduleService classScheduleService
    ) {
        this.classScheduleService = classScheduleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<List<ClassScheduleResponse>> findAllResponse(
            @PathVariable UUID classroomId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        List<ClassScheduleResponse> response =
                classScheduleService.findAllResponseByClassroom(userId, classroomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> save(
            @PathVariable UUID classroomId,
            @RequestBody @Valid ClassScheduleRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        ClassSchedule response =
                classScheduleService.save(userId, classroomId, request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> update(
            @PathVariable UUID classroomId,
            @PathVariable("id") UUID classScheduleId,
            @RequestBody @Valid ClassScheduleRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        classScheduleService.update(userId, classroomId, classScheduleId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> deleteById(
            @PathVariable UUID classroomId,
            @PathVariable("id") UUID classScheduleId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        classScheduleService.deleteById(userId, classroomId, classScheduleId);
        return ResponseEntity.noContent().build();
    }
}
