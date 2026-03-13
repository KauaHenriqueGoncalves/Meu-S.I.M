package com.system.application.modules.identity.schooladmin.repository;

import com.system.application.modules.identity.schooladmin.SchoolAdmin;
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

    @Query("SELECT sa.school.id FROM SchoolAdmin sa WHERE sa.user.id = :userId")
    Optional<UUID> findSchoolIdByUserId(@Param("userId") UUID userId);

    @Query("""
    SELECT sa
    FROM SchoolAdmin sa
    JOIN FETCH sa.user u
    WHERE sa.user.isActive = false 
        AND u.createdAt < :limit
    """)
    List<SchoolAdmin> findInactiveOlderThan(@Param("limit") Instant limit);
}
