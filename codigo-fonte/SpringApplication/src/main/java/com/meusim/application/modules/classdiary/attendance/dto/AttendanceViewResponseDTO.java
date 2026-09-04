package com.meusim.application.modules.classdiary.attendance.dto;

import com.meusim.application.modules.classdiary.attendance.Attendance;
import java.util.List;
import java.util.UUID;

public record AttendanceViewResponseDTO(
        UUID id,
        UUID studentId,
        String studentName,
        String status,
        String content
) {
    public static AttendanceViewResponseDTO of(Attendance a) {
        return new AttendanceViewResponseDTO(
                a.getId(),
                a.getStudentId(),
                a.getStudentName(),
                a.getStatus().getName(),
                a.getContent()
        );
    }

    public static List<AttendanceViewResponseDTO> of(List<Attendance> list) {
        return list.stream().map(AttendanceViewResponseDTO::of).toList();
    }
}
