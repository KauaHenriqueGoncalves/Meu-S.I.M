package com.system.application.modules.academic.classtype.service;

import com.system.application.modules.academic.classtype.ClassType;
import com.system.application.modules.academic.classtype.repository.ClassTypeRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ClassTypeServiceImpl implements ClassTypeService {
    private static final Logger log =
            LoggerFactory.getLogger(ClassTypeServiceImpl.class);
    private final ClassTypeRepository classTypeRepository;

    public ClassTypeServiceImpl(
            ClassTypeRepository classTypeRepository
    ) {
        this.classTypeRepository = classTypeRepository;
    }

    @Override
    @Cacheable(value = "class_types")
    public Set<ClassType> findAll() {
        return new HashSet<>(classTypeRepository.findAll());
    }

    @Override
    @Cacheable(key = "#id", value = "class_type_id")
    public ClassType findById(Long id) {
        return classTypeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tipo da turma não encontrado. [classScheduleId={}]", id);
                    return new NotFoundObjectException("Não achou o Tipo da Classe");
                });
    }
}
