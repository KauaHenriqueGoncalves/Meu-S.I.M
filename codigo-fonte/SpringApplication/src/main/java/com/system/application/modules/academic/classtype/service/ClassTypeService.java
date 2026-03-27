package com.system.application.modules.academic.classtype.service;

import com.system.application.modules.academic.classtype.ClassType;

import java.util.Set;

public interface ClassTypeService {
    Set<ClassType> findAll();
    ClassType findById(Long id);
}
