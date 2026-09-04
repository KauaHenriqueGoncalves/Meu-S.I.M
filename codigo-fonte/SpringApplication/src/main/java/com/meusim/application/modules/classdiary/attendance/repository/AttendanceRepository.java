package com.meusim.application.modules.classdiary.attendance.repository;

import com.meusim.application.modules.classdiary.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findAllByLessonId(UUID lessonId);
}
