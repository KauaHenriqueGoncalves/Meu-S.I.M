package com.system.application.domain.classroom.service;

import com.system.application.domain.classroom.Classroom;
import com.system.application.domain.classroom.dto.ClassroomRequest;
import com.system.application.domain.classroom.dto.ClassroomResponse;
import com.system.application.domain.classroom.dto.ClassroomSimpleResponse;
import com.system.application.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ClassroomService {
    PageResponse<ClassroomSimpleResponse> findAllSimple(UUID userId, Pageable pageable);
    ClassroomResponse findById(UUID userId, UUID classroomId);
    Classroom findByIdEntity(UUID classroomId);
    UUID save(UUID userId, ClassroomRequest request);
    UUID update(UUID userId, UUID classroomId, ClassroomRequest request);
    void addStudent(UUID userId, UUID classroomId, UUID studentId);
    void removeStudent(UUID userId, UUID classroomId, UUID studentId);
    void deleteById(UUID userId, UUID classroomId);
}
