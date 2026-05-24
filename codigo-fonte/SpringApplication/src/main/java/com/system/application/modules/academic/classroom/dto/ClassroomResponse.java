package com.system.application.modules.academic.classroom.dto;

import com.system.application.modules.academic.classroom.Classroom;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record ClassroomResponse(

        UUID id,
        String classTypeName,
        String subjectName,
        String name

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static ClassroomResponse of(Classroom c) {
        return new ClassroomResponse(
                c.getId(),
                c.getClassType().getName(),
                c.getSubject().getName(),
                c.getName()
        );
    }
}
