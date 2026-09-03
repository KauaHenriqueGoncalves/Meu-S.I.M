package com.meusim.application.modules.classdiary.attendance.service;

import com.meusim.application.modules.classdiary.attendance.Attendance;
import com.meusim.application.modules.classdiary.attendance.dto.CreateAttendanceRequestDTO;
import com.meusim.application.modules.classdiary.attendance.repository.AttendanceRepository;
import com.meusim.application.modules.classdiary.lesson.Lesson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);
    private final AttendanceRepository attendanceRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public List<Attendance> findAllByLessonId(UUID lessonId) {
        return List.of();
    }

    @Override
    public List<Attendance> findAllByLessonIdWithCache(UUID lessonId) {
        return List.of();
    }

    @Override
    public List<Attendance> createAll(Lesson lessonEntity, List<CreateAttendanceRequestDTO> list) {
        return List.of();
    }
}
