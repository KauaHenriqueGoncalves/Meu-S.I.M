package com.system.application.core.classtype.service;

import com.system.application.core.classtype.ClassType;

import java.util.Set;

public interface ClassTypeService {
    Set<ClassType> findAll();
    ClassType findById(Long id);
}
