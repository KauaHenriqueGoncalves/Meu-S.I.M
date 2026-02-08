package com.system.application.domain.schoolAdmin.repository;

import com.system.application.domain.schoolAdmin.SchoolAdmin;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolAdminRepository extends CrudRepository<SchoolAdmin, UUID> {
    Optional<SchoolAdmin> findByUserId_Id(UUID id);

    @Query("SELECT sa.schoolId.id FROM SchoolAdmin sa WHERE sa.userId.id = :userId")
    Optional<UUID> findSchoolIdByUserId(@Param("userId") UUID userId);

    @Query("SELECT sa FROM SchoolAdmin sa WHERE sa.userId.isActive = false")
    List<SchoolAdmin> findInactiveProfiles();
}
