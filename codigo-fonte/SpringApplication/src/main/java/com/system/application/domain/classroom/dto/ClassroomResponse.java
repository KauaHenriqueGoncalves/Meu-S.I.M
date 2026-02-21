package com.system.application.domain.classroom.dto;

import com.system.application.domain.classType.ClassType;
import com.system.application.domain.student.Student;
import com.system.application.domain.student.dto.StudentResponse;
import com.system.application.domain.subject.Subject;
import com.system.application.domain.subject.dto.SubjectResponse;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public record ClassroomResponse(
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
