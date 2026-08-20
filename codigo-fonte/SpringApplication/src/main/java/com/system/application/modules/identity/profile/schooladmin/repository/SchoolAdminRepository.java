package com.system.application.modules.identity.profile.schooladmin.repository;

import com.system.application.modules.identity.profile.schooladmin.SchoolAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolAdminRepository extends CrudRepository<SchoolAdmin, UUID> {
    Optional<SchoolAdmin> findByUserId(UUID userId);
    List<SchoolAdmin> findAllBySchoolId(UUID schoolId);
    int countBySchoolId(UUID schoolId);

    @Query("""
    SELECT sa
    FROM SchoolAdmin sa
    JOIN FETCH sa.user u
    WHERE sa.user.isActive = false 
        AND u.createdAt < :limit
    """)
    List<SchoolAdmin> findInactiveOlderThan(@Param("limit") Instant limit);

    @Query("""
        SELECT sa FROM SchoolAdmin sa
        WHERE sa.school.id = :schoolId
        AND (:name IS NULL OR LOWER(sa.user.username) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))
    """)
    Page<SchoolAdmin> findAllBySchoolIdAndName(
            @Param("schoolId") UUID schoolId,
            @Param("name") String name,
            Pageable pageable);
}
