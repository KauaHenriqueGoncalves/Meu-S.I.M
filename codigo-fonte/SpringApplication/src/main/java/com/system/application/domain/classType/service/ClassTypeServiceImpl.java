package com.system.application.domain.classType.service;

import com.system.application.domain.classType.ClassType;
import com.system.application.domain.classType.repository.ClassTypeRepository;
import com.system.application.shared.exception.NotFoundObjectException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ClassTypeServiceImpl implements ClassTypeService {
    private final ClassTypeRepository classTypeRepository;

    public ClassTypeServiceImpl(ClassTypeRepository classTypeRepository) {
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
        //OBS: não testado
        return classTypeRepository.findById(id).orElseThrow(
                () -> new NotFoundObjectException("Não achou o Tipo da Classe")
        );
    }
}
