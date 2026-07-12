package com.system.application.modules.academic.classroom.dto;

import com.system.application.modules.academic.classroom.Classroom;
import com.system.application.modules.academic.classtype.ClassType;
import com.system.application.modules.academic.student.Student;
import com.system.application.modules.academic.subject.dto.SubjectResponse;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record ClassroomDetailResponse(

        UUID id,
        ClassType classType,
        SubjectResponse subject,
        String name,
        Integer maxStudents,
        String description,
        List<ClassroomViewStudentResponse> students

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static ClassroomDetailResponse of(Classroom c) {
        return new ClassroomDetailResponse(
                c.getId(),
                c.getClassType(),
                new SubjectResponse(
                        c.getSubject().getId(),
                        c.getSubject().getName()
                ),
                c.getName(),
                c.getMaxStudents(),
                c.getDescription(),
                c.getStudents()
                        .stream()
                        .sorted(Comparator.comparing(Student::getName))
                        .map(ClassroomViewStudentResponse::of)
                        .toList()
        );
    }
}
