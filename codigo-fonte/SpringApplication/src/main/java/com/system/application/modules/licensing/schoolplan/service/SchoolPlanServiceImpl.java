package com.system.application.modules.licensing.schoolplan.service;

import com.system.application.modules.licensing.schoolplan.SchoolPlan;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanRequest;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanResponse;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanSimpleResponse;
import com.system.application.modules.licensing.schoolplan.dto.UpdateSchoolPlanRequest;
import com.system.application.modules.licensing.schoolplan.repository.SchoolPlanRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class SchoolPlanServiceImpl implements SchoolPlanService {
    private final SchoolPlanRepository schoolPlanRepository;

    public SchoolPlanServiceImpl(
            SchoolPlanRepository schoolPlanRepository
    ) {
        this.schoolPlanRepository = schoolPlanRepository;
    }

    @Override
    public List<SchoolPlanResponse> findAll() {
        return schoolPlanRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(SchoolPlan::getMonthlyPrice))
                .map(
                        sp -> new SchoolPlanResponse(
                                sp.getId(),
                                sp.getName(),
                                sp.getMonthlyPrice(),
                                sp.getMaxStudents(),
                                sp.getMaxCollaborators(),
                                sp.getMaxLegalGuardian(),
                                sp.getMaxSchoolAdmin(),
                                sp.getActive())
                )
                .toList();
    }

    @Override
    public List<SchoolPlanSimpleResponse> findAllSimple() {
        return schoolPlanRepository
                .findAll()
                .stream()
                .filter(SchoolPlan::getActive)
                .sorted(Comparator.comparing(SchoolPlan::getMonthlyPrice))
                .map(
                        sp -> new SchoolPlanSimpleResponse(
                                sp.getId(),
                                sp.getName(),
                                sp.getMonthlyPrice(),
                                sp.getMaxStudents(),
                                sp.getMaxCollaborators(),
                                sp.getMaxLegalGuardian(),
                                sp.getMaxSchoolAdmin())
                )
                .toList();
    }

    @Override
    public SchoolPlan findById(UUID id) {
        return schoolPlanRepository.findById(id)
                .orElseThrow(() -> new NotFoundObjectException("Não encontrou o Plano"));
    }

    @Override
    @Transactional
    public SchoolPlan save(SchoolPlanRequest request) {
        SchoolPlan schoolPlan = new SchoolPlan(
                null,
                request.name(),
                normalizePercent(request.monthlyPrice()),
                request.maxStudents(),
                request.maxCollaborators(),
                request.maxLegalGuardian(),
                request.maxSchoolAdmin(),
                true
        );
        schoolPlan = schoolPlanRepository.save(schoolPlan);
        return schoolPlan;
    }

    @Override
    @Transactional
    public void update(UUID id, UpdateSchoolPlanRequest request) {
        SchoolPlan schoolPlan = findById(id);
        schoolPlan.setName(request.name());
        schoolPlan.setMonthlyPrice(request.monthlyPrice());
        schoolPlan.setMaxStudents(request.maxStudents());
        schoolPlan.setMaxCollaborators(request.maxCollaborators());
        schoolPlan.setMaxLegalGuardian(request.maxLegalGuardian());
        schoolPlan.setMaxSchoolAdmin(request.maxSchoolAdmin());
        schoolPlan.setActive(request.isActive());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        schoolPlanRepository.deleteById(id);
    }

    private BigDecimal normalizePercent(BigDecimal percent) {
        return percent.setScale(2, RoundingMode.HALF_UP);
    }
}
