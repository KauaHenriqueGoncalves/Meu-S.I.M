package com.system.application.domain.student.controller;

import com.system.application.domain.student.Student;
import com.system.application.domain.student.dto.StudentDetailResponse;
import com.system.application.domain.student.dto.StudentRequest;
import com.system.application.domain.student.dto.StudentResponse;
import com.system.application.domain.student.dto.UpdateStudentRequest;
import com.system.application.domain.student.service.StudentService;
import com.system.application.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(
            StudentService studentService
    ) {
        this.studentService = studentService;
    }

    @GetMapping("/f")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<PageResponse<StudentResponse>> findAllBySchool(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        PageResponse<StudentResponse> response =
                studentService.findAllResponseBySchool(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/f/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<StudentDetailResponse> findById(
            @PathVariable("id") UUID studentId
    ) {
        StudentDetailResponse response =
                studentService.findResponseDetailById(studentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/c")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> create(
            @RequestBody @Valid StudentRequest request,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        Student student = studentService.save(userId, request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(student.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/c/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> update(
            @PathVariable("id") UUID studentId,
            @RequestBody @Valid UpdateStudentRequest updateRequest,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        studentService.update(userId, studentId, updateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/c/{id}")
    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    public ResponseEntity<Void> deleteById(
            @PathVariable("id") UUID studentId,
            JwtAuthenticationToken token
    ) {
        UUID userId = UUID.fromString(token.getName());
        studentService.deleteById(userId, studentId);
        return ResponseEntity.ok().build();
    }
}
