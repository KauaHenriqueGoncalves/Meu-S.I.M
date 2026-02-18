package com.system.application.domain.classType.service;

import com.system.application.domain.classType.ClassType;

import java.util.Set;

public interface ClassTypeService {
    Set<ClassType> findAll();
    ClassType findById(Long id);
}
