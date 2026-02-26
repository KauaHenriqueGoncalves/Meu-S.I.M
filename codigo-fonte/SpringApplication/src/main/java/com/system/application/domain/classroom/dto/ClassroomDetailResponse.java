package com.system.application.domain.classroom.dto;

import com.system.application.domain.classtype.ClassType;
import com.system.application.domain.subject.dto.SubjectResponse;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public record ClassroomDetailResponse(
        UUID id,
        ClassType classType,
        SubjectResponse subject,
        String name,
        Integer maxStudents,
        Set<ClassroomViewStudentResponse> students
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
