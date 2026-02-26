package com.system.application.domain.student.service;

import com.system.application.domain.student.Student;
import com.system.application.domain.student.dto.StudentDetailResponse;
import com.system.application.domain.student.dto.StudentRequest;
import com.system.application.domain.student.dto.StudentResponse;
import com.system.application.domain.student.dto.UpdateStudentRequest;
import com.system.application.shared.dto.PageResponse;

import java.util.UUID;

public interface StudentService {
    PageResponse<StudentResponse> findAllResponseBySchool(UUID userId, int page, int size);
    Student findById(UUID studentId);
    StudentDetailResponse findResponseDetailById(UUID studentId);
    Student save(UUID userId, StudentRequest request);
    void update(UUID userId, UUID studentId, UpdateStudentRequest updateRequest);
    void deleteById(UUID userId, UUID studentId);
}
