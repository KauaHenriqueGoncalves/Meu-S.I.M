package com.system.application.modules.licensing.schoolplan.service;

import com.system.application.modules.licensing.schoolplan.SchoolPlan;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanRequest;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanResponse;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanSimpleResponse;
import com.system.application.modules.licensing.schoolplan.dto.UpdateSchoolPlanRequest;
import com.system.application.modules.licensing.schoolplan.repository.SchoolPlanRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class SchoolPlanServiceImpl implements SchoolPlanService {
    private static final Logger log =
            LoggerFactory.getLogger(SchoolPlanServiceImpl.class);

    private final SchoolPlanRepository schoolPlanRepository;

    public SchoolPlanServiceImpl(
            SchoolPlanRepository schoolPlanRepository
    ) {
        this.schoolPlanRepository = schoolPlanRepository;
    }

    @Override
    public List<SchoolPlanResponse> findAll() {
        log.info("Buscando todos os planos da escola.");

        List<SchoolPlanResponse> response = schoolPlanRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(SchoolPlan::getMonthlyPrice))
                .map(sp -> new SchoolPlanResponse(
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

        log.info("Planos da escola encontrados. [total={}]", response.size());

        return response;
    }

    @Override
    public List<SchoolPlanSimpleResponse> findAllSimple() {
        log.info("Buscando planos ativos da escola.");

        List<SchoolPlanSimpleResponse> response = schoolPlanRepository.findAll()
                .stream()
                .filter(SchoolPlan::getActive)
                .sorted(Comparator.comparing(SchoolPlan::getMonthlyPrice))
                .map(sp -> new SchoolPlanSimpleResponse(
                        sp.getId(),
                        sp.getName(),
                        sp.getMonthlyPrice(),
                        sp.getMaxStudents(),
                        sp.getMaxCollaborators(),
                        sp.getMaxLegalGuardian(),
                        sp.getMaxSchoolAdmin())
                )
                .toList();

        log.info("Planos ativos da escola encontrados. [total={}]", response.size());

        return response;
    }

    @Override
    public SchoolPlan findById(UUID id) {
        return schoolPlanRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Plano da escola nao encontrado. [schoolPlanId={}]", id);
                    return new NotFoundObjectException("Nao encontrou o plano");
                });
    }

    @Override
    @Transactional
    public SchoolPlan save(SchoolPlanRequest request) {
        log.info("Iniciando cadastro de plano da escola. [nome={}] [preco={}] [maxEstudantes={}] [maxColaboradores={}]",
                request.name(), request.monthlyPrice(), request.maxStudents(), request.maxCollaborators());

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

        log.info("Plano da escola cadastrado com sucesso. [schoolPlanId={}] [nome={}] [preco={}]",
                schoolPlan.getId(), schoolPlan.getName(), schoolPlan.getMonthlyPrice());

        return schoolPlan;
    }

    @Override
    @Transactional
    public void update(UUID id, UpdateSchoolPlanRequest request) {
        log.info("Iniciando atualizacao de plano da escola. [schoolPlanId={}] [nome={}] [preco={}] [ativo={}]",
                id, request.name(), request.monthlyPrice(), request.isActive());

        SchoolPlan schoolPlan = findById(id);

        schoolPlan.setName(request.name());
        schoolPlan.setMonthlyPrice(request.monthlyPrice());
        schoolPlan.setMaxStudents(request.maxStudents());
        schoolPlan.setMaxCollaborators(request.maxCollaborators());
        schoolPlan.setMaxLegalGuardian(request.maxLegalGuardian());
        schoolPlan.setMaxSchoolAdmin(request.maxSchoolAdmin());
        schoolPlan.setActive(request.isActive());

        log.info("Plano da escola atualizado com sucesso. [schoolPlanId={}] [nome={}] [ativo={}]",
                id, request.name(), request.isActive());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusao de plano da escola. [schoolPlanId={}]", id);

        schoolPlanRepository.deleteById(id);

        log.info("Plano da escola excluido com sucesso. [schoolPlanId={}]", id);
    }

    private BigDecimal normalizePercent(BigDecimal percent) {
        return percent.setScale(2, RoundingMode.HALF_UP);
    }
}
