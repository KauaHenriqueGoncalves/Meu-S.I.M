package com.system.application.domain.student.service;

import com.system.application.domain.student.dto.StudentRequest;
import com.system.application.domain.student.dto.StudentResponse;
import com.system.application.domain.student.dto.UpdateStudentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentService {
    Page<StudentResponse> findAllBySchoolAdminId(UUID adminId, Pageable pageable);
    StudentResponse findById(UUID studentId);
    UUID save(UUID adminId, StudentRequest studentRequest);
    UUID update(UUID adminId, UUID studentId, UpdateStudentRequest updateRequest);
    void deleteById(UUID adminId, UUID studentId);
}
