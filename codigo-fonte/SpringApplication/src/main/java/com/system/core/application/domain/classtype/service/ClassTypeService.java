package com.system.core.application.domain.classtype.service;

import com.system.core.application.domain.classtype.ClassType;

import java.util.Set;

public interface ClassTypeService {
    Set<ClassType> findAll();
    ClassType findById(Long id);
}
