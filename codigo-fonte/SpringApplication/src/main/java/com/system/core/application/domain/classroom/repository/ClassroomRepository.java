package com.system.core.application.domain.classroom.repository;

import com.system.core.application.domain.classroom.Classroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    Page<Classroom> findAllBySchoolId(UUID schoolId, Pageable pageable);
}
