package com.system.application.modules.academic.subject.repository;

import com.system.application.modules.academic.subject.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    Page<Subject> findAllBySchoolId(UUID schoolId, Pageable pageable);
}
