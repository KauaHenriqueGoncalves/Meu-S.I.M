package com.system.core.application.domain.collaborator.repository;

import com.system.core.application.domain.collaborator.Collaborator;
import com.system.core.application.domain.collaborator.repository.projection.CollaboratorListView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, UUID> {
    Boolean existsByIdAndSchool_Id(UUID collaboratorId, UUID schoolId);

    @Query("""
            SELECT
                c.id AS id,
                u.username AS username,
                c.specialty AS specialty,
                c.workload AS workload
            FROM Collaborator c
            JOIN c.user u
            WHERE c.school.id = :schoolId
            """)
    Page<CollaboratorListView> findAllBySchoolId(@Param("schoolId") UUID schoolId, Pageable pageable);
}
