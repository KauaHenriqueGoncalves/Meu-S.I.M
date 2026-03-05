package com.system.application.core.schoolplan.repository;

import com.system.application.core.schoolplan.SchoolPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SchoolPlanRepository extends JpaRepository<SchoolPlan, UUID> {
}
