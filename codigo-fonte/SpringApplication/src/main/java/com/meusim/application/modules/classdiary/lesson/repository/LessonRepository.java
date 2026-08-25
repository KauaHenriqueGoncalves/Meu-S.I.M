package com.meusim.application.modules.classdiary.lesson.repository;

import com.meusim.application.modules.classdiary.lesson.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    Page<Lesson> findAllByClassroomId(UUID classroomId, Pageable pageable);
    List<Lesson> findByClassroomIdAndLessonDateBetween(UUID classroomId, LocalDate start, LocalDate end);
}
