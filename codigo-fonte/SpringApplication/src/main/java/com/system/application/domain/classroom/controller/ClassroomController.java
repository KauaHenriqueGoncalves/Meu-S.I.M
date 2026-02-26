package com.system.application.domain.classroom.controller;

import com.system.application.domain.classroom.dto.GetStudentIdInClassroomRequest;
import com.system.application.domain.classroom.dto.ClassroomRequest;
import com.system.application.domain.classroom.dto.ClassroomResponse;
import com.system.application.domain.classroom.dto.ClassroomSimpleResponse;
import com.system.application.domain.classroom.service.ClassroomService;
import com.system.application.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/classroom")
public class ClassroomController {
    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @GetMapping("/f")
    public ResponseEntity<PageResponse<ClassroomSimpleResponse>> findAllBySchool(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                 @RequestParam(value = "size", defaultValue = "3") int size,
                                                                                 JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        PageResponse<ClassroomSimpleResponse> pageResponse = classroomService.findAllSimple(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(pageResponse);
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @GetMapping("/f/{id}")
    public ResponseEntity<ClassroomResponse> findById(@PathVariable("id") UUID classroomId,
                                                      JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        ClassroomResponse response = classroomService.findById(userId, classroomId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid ClassroomRequest request,
                                     JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        UUID classroomId = classroomService.save(userId, request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(classroomId)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateClassroom(@PathVariable("id") UUID classroomId,
                                                @RequestBody @Valid ClassroomRequest request,
                                                JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        classroomService.update(userId, classroomId, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PostMapping("/a/{id}")
    public ResponseEntity<Void> addStudent(@PathVariable("id") UUID classroomId,
                                           @RequestBody GetStudentIdInClassroomRequest studentId,
                                           JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        classroomService.addStudent(userId, classroomId, studentId.studentId());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PostMapping("/r/{id}")
    public ResponseEntity<Void> removeStudent(@PathVariable("id") UUID classroomId,
                                              @RequestBody GetStudentIdInClassroomRequest studentId,
                                              JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        classroomService.removeStudent(userId, classroomId, studentId.studentId());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID classroomId,
                                              JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        classroomService.deleteById(userId, classroomId);
        return ResponseEntity.noContent().build();
    }
}
