package com.system.application.modules.licensing.schoolplan.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.modules.licensing.schoolplan.SchoolPlan;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanRequest;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanResponse;
import com.system.application.modules.licensing.schoolplan.dto.SchoolPlanSimpleResponse;
import com.system.application.modules.licensing.schoolplan.dto.UpdateSchoolPlanRequest;
import com.system.application.modules.licensing.schoolplan.repository.SchoolPlanRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.services.cache.CacheService;
import com.system.application.shared.services.cache.keys.CacheKeys;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SchoolPlanServiceImpl implements SchoolPlanService {
    private static final Logger log =
            LoggerFactory.getLogger(SchoolPlanServiceImpl.class);

    private final SchoolPlanRepository schoolPlanRepository;
    private final CacheService cacheService;

    private static final Duration SCHOOLPLAN_TTL = Duration.ofHours(100);

    public SchoolPlanServiceImpl(
            SchoolPlanRepository schoolPlanRepository,
            CacheService cacheService
    ) {
        this.schoolPlanRepository = schoolPlanRepository;
        this.cacheService = cacheService;
    }

    @Override
    public List<SchoolPlanResponse> findAll() {
        log.info("Buscando todos os planos da escola.");

        String key = CacheKeys.schoolPlan("List");

        Optional<List<SchoolPlanResponse>> cacheResponse = cacheService.get(
                key,
                new TypeReference<>() {}
        );

        if (cacheResponse.isPresent()) {
            log.info("Planos da escola encontrados para System no cache. [total={}]",
                    cacheResponse.get().size());
            return cacheResponse.get();
        }

        List<SchoolPlanResponse> response = schoolPlanRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(SchoolPlan::getMonthlyPrice))
                .map(SchoolPlanResponse::of)
                .toList();

        log.info("Planos da escola encontrados para System. [total={}]", response.size());

        cacheService.set(key, response, SCHOOLPLAN_TTL);

        return response;
    }

    @Override
    public List<SchoolPlanSimpleResponse> findAllSimple() {
        log.info("Buscando planos ativos para escolas.");

        String key = CacheKeys.schoolPlan("ListToClient");

        Optional<List<SchoolPlanSimpleResponse>> cacheResponse = cacheService.get(
                key,
                new TypeReference<>() {}
        );

        if (cacheResponse.isPresent()) {
            log.info("Planos ativos para escolas encontrados no cache. [total={}]",
                    cacheResponse.get().size());
            return cacheResponse.get();
        }

        List<SchoolPlanSimpleResponse> response = schoolPlanRepository.findAll()
                .stream()
                .filter(SchoolPlan::getActive)
                .sorted(Comparator.comparing(SchoolPlan::getMonthlyPrice))
                .map(SchoolPlanSimpleResponse::of)
                .toList();

        log.info("Planos ativos para escolas encontrados. [total={}]", response.size());

        cacheService.set(key, response, SCHOOLPLAN_TTL);

        return response;
    }

    @Override
    public SchoolPlan findById(UUID id) {
        log.info("Buscando plano pelo id. [schoolPlanId={}]", id);

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

        String key = CacheKeys.schoolPlanPattern();

        log.info("Apagando todos os caches de schoolPlan. [key={}]", key);

        cacheService.evictByPattern(key);

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

        String key = CacheKeys.schoolPlanPattern();

        log.info("Apagando todos os caches de schoolPlan. [key={}]", key);

        cacheService.evictByPattern(key);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusao de plano da escola. [schoolPlanId={}]", id);

        schoolPlanRepository.deleteById(id);

        log.info("Plano da escola excluido com sucesso. [schoolPlanId={}]", id);

        String key = CacheKeys.schoolPlanPattern();

        log.info("Apagando todos os caches de schoolPlan. [key={}]", key);

        cacheService.evictByPattern(key);
    }

    private BigDecimal normalizePercent(BigDecimal percent) {
        return percent.setScale(2, RoundingMode.HALF_UP);
    }
}
