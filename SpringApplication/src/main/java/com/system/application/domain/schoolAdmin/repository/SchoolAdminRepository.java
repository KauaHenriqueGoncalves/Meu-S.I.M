package com.system.application.domain.schoolAdmin.repository;

import com.system.application.domain.schoolAdmin.SchoolAdmin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolAdminRepository extends CrudRepository<SchoolAdmin, UUID> {
    Optional<SchoolAdmin> findByUserId_Id(UUID id);
}
