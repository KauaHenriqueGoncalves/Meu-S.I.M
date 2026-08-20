package com.system.application.modules.identity.profile.systemadmin.repository;

import com.system.application.modules.identity.profile.systemadmin.SystemAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SystemAdminRepository extends JpaRepository<SystemAdmin, UUID> {
    Optional<SystemAdmin> findByUserCpfAndUserEmail(String cpf, String email);
}
