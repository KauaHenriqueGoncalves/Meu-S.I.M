package com.system.application.domain.classroom.repository;

import com.system.application.domain.classroom.Classroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    Page<Classroom> findAllBySchoolId(UUID schoolId, Pageable pageable);

    @Query("""
    SELECT c FROM Classroom c
    JOIN c.students s
    WHERE s.id = :studentId
    """)
    List<Classroom> findAllResponseByStudentId(@Param("studentId") UUID studentId);
}
