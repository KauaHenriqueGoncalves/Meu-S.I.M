package com.system.application.modules.identity.collaborator.repository;

import com.system.application.modules.identity.collaborator.Collaborator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, UUID> {
    long countBySchoolId(UUID schoolId);

    @Query("""
        SELECT c FROM Collaborator c
        WHERE c.school.id = :schoolId
        AND (:name IS NULL OR LOWER(c.user.username) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))
    """)
    Page<Collaborator> findAllBySchoolIdAndName(
            @Param("schoolId") UUID schoolId,
            @Param("name") String name,
            Pageable pageable
    );
}
