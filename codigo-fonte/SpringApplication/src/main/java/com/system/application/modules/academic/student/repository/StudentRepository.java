package com.system.application.modules.academic.student.repository;

import com.system.application.modules.academic.student.Student;
import com.system.application.modules.identity.legalguardian.LegalGuardian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Page<Student> findAllBySchoolId(UUID schoolId, Pageable pageable);
    List<Student> findAllByLegalGuardianId(UUID legalGuardianId);
    long countBySchoolId(UUID schoolId);

    @Query("""
        SELECT s FROM Student s
        WHERE s.school.id = :schoolId
        AND (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))
    """)
    Page<Student> findAllBySchoolIdAndName(
            @Param("schoolId") UUID schoolId,
            @Param("name") String name,
            Pageable pageable
    );
}
