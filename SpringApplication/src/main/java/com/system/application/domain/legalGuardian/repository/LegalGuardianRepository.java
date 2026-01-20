package com.system.application.domain.legalGuardian.repository;

import com.system.application.domain.legalGuardian.LegalGuardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LegalGuardianRepository extends JpaRepository<LegalGuardian, UUID> {
}
