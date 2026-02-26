package com.system.application.domain.classroom.controller;

import com.system.application.domain.classroom.Classroom;
import com.system.application.domain.classroom.dto.GetStudentIdInClassroomRequest;
import com.system.application.domain.classroom.dto.ClassroomRequest;
import com.system.application.domain.classroom.dto.ClassroomDetailResponse;
import com.system.application.domain.classroom.dto.ClassroomResponse;
import com.system.application.domain.classroom.service.ClassroomService;
import com.system.application.shared.dto.PageResponse;
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
@RequestMapping("/classroom")
public class ClassroomController {
    private final ClassroomService classroomService;

    public ClassroomController(
            ClassroomService classroomService
    ) {
        this.classroomService = classroomService;
    }

    @GetMapping("/f")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<PageResponse<ClassroomResponse>> findAllResponseBySchool(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        PageResponse<ClassroomResponse> pageResponse =
                classroomService.findAllResponseBySchool(userId, page, size);
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/f/s/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<List<ClassroomResponse>> findAllResponseByStudentId(
            @PathVariable("id") UUID studentId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        List<ClassroomResponse> response =
                classroomService.findAllResponseByStudentId(userId, studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/f/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<ClassroomDetailResponse> findDetailResponseById(
            @PathVariable("id") UUID classroomId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        ClassroomDetailResponse response =
                classroomService.findDetailResponseById(userId, classroomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> save(
            @RequestBody @Valid ClassroomRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        Classroom classroom = classroomService.save(userId, request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(classroom.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> update(
            @PathVariable("id") UUID classroomId,
            @RequestBody @Valid ClassroomRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        classroomService.update(userId, classroomId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/a/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> addStudent(
            @PathVariable("id") UUID classroomId,
            @RequestBody GetStudentIdInClassroomRequest studentId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        classroomService.addStudent(userId, classroomId, studentId.studentId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/r/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> removeStudent(
            @PathVariable("id") UUID classroomId,
            @RequestBody GetStudentIdInClassroomRequest studentId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        classroomService.removeStudent(userId, classroomId, studentId.studentId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") UUID classroomId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        classroomService.deleteById(userId, classroomId);
        return ResponseEntity.noContent().build();
    }
}
