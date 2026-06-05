package com.system.application.modules.school.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.modules.school.School;
import com.system.application.modules.school.dto.SchoolRequest;
import com.system.application.modules.school.repository.SchoolRepository;
import com.system.application.shared.exception.EntityAlreadyExistsException;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.services.cache.CacheService;
import com.system.application.shared.services.cache.keys.CacheKeys;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class SchoolServiceImpl implements SchoolService {
    private static final Logger log =
            LoggerFactory.getLogger(SchoolServiceImpl.class);

    private final SchoolRepository schoolRepository;
    private final CacheService cacheService;

    public SchoolServiceImpl(
            SchoolRepository schoolRepository,
            CacheService cacheService
    ) {
        this.schoolRepository = schoolRepository;
        this.cacheService = cacheService;
    }

    @Override
    public School findById(UUID schoolId) {
        log.info("Procurando School pelo ID. [schoolId={}]" , schoolId);

        return schoolRepository.findById(schoolId)
                .orElseThrow(() ->{
                    log.warn("Escola não foi encontrada. [schoolId={}]", schoolId);
                    return new NotFoundObjectException("Escola não encontrada");
                });
    }

    @Override
    public School findByUserId(UUID userId) {
        log.info("Procurando a escola do ID usuario. [userId={}]", userId);

        String key = CacheKeys.school(userId, "byUser");

        Optional<School> cacheResponse = cacheService.get(
                key,
                new TypeReference<>() {}
        );

        if (cacheResponse.isPresent()) {
            log.info("Encontrado a escola pelo ID usuario. [userId={}] [schoolId={}]",
                    userId, cacheResponse.get().getId());
            return cacheResponse.get();
        }

        School response = schoolRepository.findSchoolByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Escola não encontrada do ID do usuário. [userId={}]", userId);
                    return new NotFoundObjectException("Escola não encontrada");
                });

        cacheService.set(key, response, Duration.ofHours(5));

        return response;
    }

    @Override
    @Transactional
    public School save(SchoolRequest request) {
        log.info("Iniciando cadastro de School. [request={}]", request);

        checkSchoolConflict(request);
        School school = new School(
                null,
                request.nameCode(),
                request.schoolName(),
                request.cnpj()
        );
        school = schoolRepository.save(school);

        log.info("School criado com sucesso. [schoolId={}]", school.getId());

        return school;
    }

    private void checkSchoolConflict(SchoolRequest request) {
        if (schoolRepository.existsByNameCode(request.nameCode())) {
            log.warn("Tentativa de cadastro com Codigo do reforco ja cadastrado. [nameCode={}]", request.nameCode());
            throw new EntityAlreadyExistsException("Código do reforço já cadastrado");
        }
        if (schoolRepository.existsByCnpj(request.cnpj())) {
            log.warn("Tentativa de cadastro com CNPJ já cadastrado. [cnpj={}]", request.cnpj());
            throw new EntityAlreadyExistsException("Cnpj já cadastrado");
        }
    }
}
