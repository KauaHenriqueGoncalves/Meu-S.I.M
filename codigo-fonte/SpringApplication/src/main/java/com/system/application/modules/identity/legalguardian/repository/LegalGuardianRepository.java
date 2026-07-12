package com.system.application.modules.identity.legalguardian.repository;

import com.system.application.modules.identity.legalguardian.LegalGuardian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LegalGuardianRepository extends JpaRepository<LegalGuardian, UUID> {
    boolean existsByIdAndSchoolId(UUID legalGuardianId, UUID schoolId);
    long countBySchoolId(UUID schoolId);

    @Query("""
        SELECT l FROM LegalGuardian l
        WHERE l.school.id = :schoolId
        AND (:name IS NULL OR LOWER(l.user.username) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))
    """)
    Page<LegalGuardian> findAllBySchoolIdAndName(
            @Param("schoolId") UUID schoolId,
            @Param("name") String name,
            Pageable pageable
    );
}
