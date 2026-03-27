package com.system.application.modules.academic.student.service;

import com.system.application.modules.academic.student.Student;
import com.system.application.modules.academic.student.dto.StudentDetailResponse;
import com.system.application.modules.academic.student.dto.StudentRequest;
import com.system.application.modules.academic.student.dto.StudentResponse;
import com.system.application.modules.academic.student.dto.UpdateStudentRequest;
import com.system.application.shared.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    PageResponse<StudentResponse> findAllResponseBySchool(UUID userId, int page, int size);
    List<StudentResponse> findAllResponseByLegalGuardian(UUID userId, UUID legalGuardianId);
    Student findById(UUID studentId);
    StudentDetailResponse findResponseDetailById(UUID studentId);
    Student save(UUID userId, StudentRequest request);
    void update(UUID userId, UUID studentId, UpdateStudentRequest updateRequest);
    void deleteById(UUID userId, UUID studentId);
}
