package com.system.application.domain.student.controller;

import com.system.application.domain.student.dto.StudentRequest;
import com.system.application.domain.student.dto.StudentResponse;
import com.system.application.domain.student.dto.UpdateStudentRequest;
import com.system.application.domain.student.service.StudentService;
import com.system.application.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @GetMapping("/f")
    public ResponseEntity<PageResponse<StudentResponse>> findAllBySchool(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                         @RequestParam(value = "size", defaultValue = "3") int size,
                                                                         JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        Page<StudentResponse> studentsPage = studentService.findAllBySchoolAdminId(adminId, PageRequest.of(page, size));
        PageResponse<StudentResponse> response = new PageResponse<>(
                studentsPage.getContent(),
                studentsPage.getNumber(),
                studentsPage.getSize(),
                studentsPage.getTotalPages(),
                studentsPage.getTotalElements(),
                studentsPage.hasNext(),
                studentsPage.hasPrevious()
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @GetMapping("/f/{id}")
    public ResponseEntity<StudentResponse> findById(@PathVariable("id") UUID studentId) {
        StudentResponse response = studentService.findById(studentId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PostMapping("/c")
    public ResponseEntity<Void> createStudent(@RequestBody @Valid StudentRequest request,
                                              JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        UUID studentId = studentService.save(adminId, request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(studentId)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @PutMapping("/c/{id}")
    public ResponseEntity<Void> updateStudent(@PathVariable("id") UUID studentId,
                                              @RequestBody @Valid UpdateStudentRequest updateRequest,
                                              JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        studentService.update(adminId, studentId, updateRequest);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_school_admin')")
    @DeleteMapping("/c/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID studentId,
                                           JwtAuthenticationToken jwtToken) {
        UUID adminId = UUID.fromString(jwtToken.getName());
        studentService.deleteById(adminId, studentId);
        return ResponseEntity.ok().build();
    }
}
