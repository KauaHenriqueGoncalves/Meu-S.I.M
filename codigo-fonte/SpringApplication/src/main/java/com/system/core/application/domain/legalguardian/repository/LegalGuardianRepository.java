package com.system.core.application.domain.legalguardian.repository;

import com.system.core.application.domain.legalguardian.LegalGuardian;
import com.system.core.application.domain.legalguardian.repository.projection.LegalGuardianListView;
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

    @Query("""
    SELECT
        lg.id AS id,
        u.username AS username,
        lg.degreeOfKinship AS degreeOfKinship
    FROM LegalGuardian lg
    JOIN lg.user u
    WHERE lg.school.id = :schoolId
    """)
    Page<LegalGuardianListView> findAllBySchoolId(@Param("schoolId") UUID schoolId, Pageable pageable);
}
