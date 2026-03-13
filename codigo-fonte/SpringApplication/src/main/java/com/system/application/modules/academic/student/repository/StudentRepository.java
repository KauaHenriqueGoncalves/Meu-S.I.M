package com.system.application.modules.academic.student.repository;

import com.system.application.modules.academic.student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Page<Student> findAllBySchoolId(UUID schoolId, Pageable pageable);
    List<Student> findAllByLegalGuardianId(UUID legalGuardianId);
}
