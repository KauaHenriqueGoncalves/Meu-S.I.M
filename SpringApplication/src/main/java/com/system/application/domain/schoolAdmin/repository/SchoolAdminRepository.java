package com.system.application.domain.schoolAdmin.repository;

import com.system.application.domain.schoolAdmin.SchoolAdmin;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SchoolAdminRepository extends CrudRepository<SchoolAdmin, UUID> {
}
