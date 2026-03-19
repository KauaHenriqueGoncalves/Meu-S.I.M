package com.system.application.modules.academic.classroom.service;

import com.system.application.modules.academic.classroom.Classroom;
import com.system.application.modules.academic.classroom.dto.ClassroomRequest;
import com.system.application.modules.academic.classroom.dto.ClassroomDetailResponse;
import com.system.application.modules.academic.classroom.dto.ClassroomResponse;
import com.system.application.shared.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface ClassroomService {
    PageResponse<ClassroomResponse> findAllResponseBySchool(UUID userId, int page, int size);
    List<ClassroomResponse> findAllResponseByStudentId(UUID userId, UUID studentId);
    Classroom findById(UUID classroomId);
    ClassroomDetailResponse findDetailResponseById(UUID userId, UUID classroomId);
    Classroom save(UUID userId, ClassroomRequest request);
    void update(UUID userId, UUID classroomId, ClassroomRequest request);
    void addStudent(UUID userId, UUID classroomId, UUID studentId);
    void removeStudent(UUID userId, UUID classroomId, UUID studentId);
    void deleteById(UUID userId, UUID classroomId);
}
