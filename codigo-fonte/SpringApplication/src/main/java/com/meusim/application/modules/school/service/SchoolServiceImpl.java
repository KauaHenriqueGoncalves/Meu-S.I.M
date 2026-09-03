package com.meusim.application.modules.school.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meusim.application.auth.service.AuthenticatedUserService;
import com.meusim.application.modules.school.School;
import com.meusim.application.modules.school.cache.SchoolCacheKeys;
import com.meusim.application.modules.school.dto.CreateSchoolRequestDTO;
import com.meusim.application.modules.school.dto.UpdateSchoolRequestDTO;
import com.meusim.application.modules.school.repository.SchoolRepository;
import com.meusim.application.modules.school.validator.SchoolValidator;
import com.meusim.application.shared.exception.BusinessException;
import com.meusim.application.shared.exception.NotFoundObjectException;
import com.meusim.application.shared.services.cache.CacheService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class SchoolServiceImpl implements SchoolService {
    private static final Logger log = LoggerFactory.getLogger(SchoolServiceImpl.class);
    private final SchoolRepository schoolRepository;
    private final SchoolValidator schoolValidator;
    private final AuthenticatedUserService authenticatedUserService;
    private final CacheService cacheService;

    public SchoolServiceImpl(
            SchoolRepository schoolRepository,
            SchoolValidator schoolValidator,
            AuthenticatedUserService authenticatedUserService,
            CacheService cacheService) {
        this.schoolRepository = schoolRepository;
        this.schoolValidator = schoolValidator;
        this.authenticatedUserService = authenticatedUserService;
        this.cacheService = cacheService;
    }

    @Override
    public School findById(UUID id) {
        log.info("Buscando escola pelo ID. [id={}]" , id);
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Escola nao foi encontrada. [id={}]", id);
                    return new NotFoundObjectException("Escola não encontrada");
                });
        log.info("Escola encontrada pelo ID. [id={}]", id);
        return school;
    }

    @Override
    public School findByIdWithCache(UUID id) {
        log.info("Buscando escola pelo ID. [id={}]" , id);
        String key = SchoolCacheKeys.byId(id);
        Optional<School> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Escola encontrada no cache. [id={}] [key={}]", id, key);
            return cache.get();
        }
        School school = findById(id);
        log.info("Escola encontrada e salva no cache. [id={}] [key={}]", id, key);
        cacheService.set(key, school, SchoolCacheKeys.TTL);
        return school;
    }

    @Override
    public School findByUserId(UUID userId) {
        log.info("Buscando escola pelo userId. [userId={}]" , userId);
        School school = schoolRepository.findSchoolByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("Escola nao encontrada do userID. [userId={}]", userId);
                    return new NotFoundObjectException("Escola não encontrada");
                });
        log.info("Escola encontrada pelo userId. [userId={}]" , userId);
        return school;
    }

    @Override
    public School findByUserIdWithCache(UUID userId) {
        School ownerSchool = findByOwnerIdWithCache();
        log.info("Buscando escola pelo userId. [userId={}] [schoolId={}]",
                userId, ownerSchool.getId());
        String key = SchoolCacheKeys.byUserId(ownerSchool.getId(), userId);
        Optional<School> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Escola encontrada no cache pelo userId. [userId={}] [schoolId={}] [key={}]",
                    userId, ownerSchool.getId(), key);
            return cache.get();
        }
        School userSchool = findByUserId(userId);
        schoolValidator.ensureSchoolSameByOwnerId(userSchool, ownerSchool);
        cacheService.set(key, userSchool, SchoolCacheKeys.TTL);
        return userSchool;
    }

    @Override
    public School findByOwnerId() {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Buscando a escola pelo ownerId. [ownerId={}]", ownerId);
        School school = findByUserId(ownerId);
        log.info("Escola encontrada pelo ownerId. [ownerId={}] [schoolId={}]",
                ownerId, school.getId());
        return school;
    }

    @Override
    public School findByOwnerIdWithCache() {
        UUID ownerId = authenticatedUserService.getOwnerId();
        log.info("Buscando a escola pelo ownerId. [ownerId={}]", ownerId);
        String key = SchoolCacheKeys.byOwnerId(ownerId);
        Optional<School> cache = cacheService.get(key, new TypeReference<>(){});
        if (cache.isPresent()) {
            log.info("Escola encontrada no cache pelo ownerId. [ownerId={}] [schoolId={}] [key={}]",
                    ownerId, cache.get().getId(), key);
            return cache.get();
        }
        School school = findByOwnerId();
        log.info("Escola encontrada pelo ownerId e salvo no cache. [ownerId={}] [schoolId={}] [key={}]",
                ownerId, school.getId(), key);
        cacheService.set(key, school, SchoolCacheKeys.TTL);
        return school;
    }

    @Override
    @Transactional
    public School create(CreateSchoolRequestDTO dto) {
        log.info("Criando o reforço escolar. [nameCode={}] [schoolName={}] [cnpj={}]",
                dto.nameCode(), dto.schoolName(), dto.cnpj());
        schoolValidator.ensureSchoolAlreadyExistNameCode(dto.nameCode());
        schoolValidator.ensureSchoolAlreadyExistCnpj(dto.cnpj());
        School school = schoolRepository.save(School.of(dto));
        log.info("Reforco escolar criado com sucesso. [schoolId={}] [nameCode={}] [schoolName={}] [cnpj={}]",
                school.getId(), school.getNameCode(), school.getSchoolName(), school.getCnpj());
        return school;
    }

    @Override
    @Transactional
    public School update(UUID id, UpdateSchoolRequestDTO dto) {
        UUID ownerId = authenticatedUserService.getOwnerId();
        School ownerSchool = findByOwnerId();
        log.info("Atualizando as informacoes do reforco escolar. [ownerId={}] [schoolId={}] [nameCodeOld={}] [schoolNameOld={}] [cnpjOld={}]",
                ownerId, ownerSchool.getId(), ownerSchool.getNameCode(), ownerSchool.getSchoolName(), ownerSchool.getCnpj());
        schoolValidator.ensureSchoolSameByOwnerId(id, ownerSchool);
        schoolValidator.ensureSchoolHasSubscription(ownerSchool);
        ownerSchool.setNameCode(dto.nameCode());
        ownerSchool.setSchoolName(dto.schoolName());
        ownerSchool.setCnpj(dto.cnpj());
        log.info("Atualizado as informacoes do reforco escolar e limpando os caches relacionados. [ownerId={}] [schoolId={}] [nameCodeNew={}] [schoolNameNew={}] [cnpjNew={}]",
                ownerId, ownerSchool.getId(), ownerSchool.getNameCode(), ownerSchool.getSchoolName(), ownerSchool.getCnpj());
        cacheService.delete(SchoolCacheKeys.byId(ownerSchool.getId()));
        cacheService.delete(SchoolCacheKeys.byOwnerId(ownerId));
        cacheService.evictByPattern(SchoolCacheKeys.byPatternByUserId(ownerSchool.getId()));
        return ownerSchool;
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        // TODO: fazer efeito cascata para os demais dados {deletar todos os dados}
        throw new BusinessException("Método não implementado");
    }
}
