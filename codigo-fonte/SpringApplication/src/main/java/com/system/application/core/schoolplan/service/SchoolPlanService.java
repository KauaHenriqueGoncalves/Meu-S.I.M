package com.system.application.core.schoolplan.service;

import com.system.application.core.schoolplan.SchoolPlan;
import com.system.application.core.schoolplan.dto.SchoolPlanRequest;
import com.system.application.core.schoolplan.dto.SchoolPlanResponse;
import com.system.application.core.schoolplan.dto.SchoolPlanSimpleResponse;
import com.system.application.core.schoolplan.dto.UpdateSchoolPlanRequest;

import java.util.List;
import java.util.UUID;

public interface SchoolPlanService {
    List<SchoolPlanResponse> findAll();
    List<SchoolPlanSimpleResponse> findAllSimple();
    SchoolPlan findById(UUID id);
    SchoolPlan save(SchoolPlanRequest request);
    void update(UUID id, UpdateSchoolPlanRequest request);
    void delete(UUID id);
}
