package com.system.application.modules.academic.classtype.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.system.application.modules.academic.classtype.ClassType;
import com.system.application.modules.academic.classtype.repository.ClassTypeRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import com.system.application.shared.services.cache.CacheService;
import com.system.application.shared.services.cache.keys.CacheKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ClassTypeServiceImpl implements ClassTypeService {
    private static final Logger log =
            LoggerFactory.getLogger(ClassTypeServiceImpl.class);

    private final ClassTypeRepository classTypeRepository;
    private final CacheService cacheService;

    private static final Duration CLASSTYPE_TTL = Duration.ofHours(100);

    public ClassTypeServiceImpl(
            ClassTypeRepository classTypeRepository,
            CacheService cacheService
    ) {
        this.classTypeRepository = classTypeRepository;
        this.cacheService = cacheService;
    }

    @Override
    public List<ClassType> findAll() {
        String key = CacheKeys.classTypeList();

        log.info("Buscando os tipos de classes");

        return cacheService.get(
                key,
                new TypeReference<List<ClassType>>() {}
        ).orElseGet(() -> {
            log.info("Buscando os tipos de classes pelo banco de dados");
            List<ClassType> list = classTypeRepository.findAll();
            cacheService.set(key, list, CLASSTYPE_TTL);
            return list;
        });
    }

    @Override
    public ClassType findById(Long id) {
        String key = CacheKeys.classType(id);

        log.info("Buscando o tipo de classe pelo id. [classTypeId={}]", id);

        return cacheService.get(
                key,
                new TypeReference<ClassType>() {}
        ).orElseGet(() -> {
            log.info("Buscando o tipo de classe pelo id no banco de dados. [classTypeId={}]", id);

            ClassType classType = classTypeRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Tipo da turma não encontrado. [classScheduleId={}]", id);
                        return new NotFoundObjectException("Não achou o Tipo da Classe");
                    });

            cacheService.set(key, classType, CLASSTYPE_TTL);

            log.info("Tipo da classe encontrado e cacheado com sucesso. [classTypeId={}]", id);

            return classType;
        });
    }
}
