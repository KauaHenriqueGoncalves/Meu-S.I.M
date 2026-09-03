package com.meusim.application.modules.licensing.schoolplan.repository;

import com.meusim.application.modules.licensing.schoolplan.SchoolPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SchoolPlanRepository extends JpaRepository<SchoolPlan, UUID> {
}
