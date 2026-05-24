package com.system.application.modules.academic.classtype.service;

import com.system.application.modules.academic.classtype.ClassType;

import java.util.List;

public interface ClassTypeService {
    List<ClassType> findAll();
    ClassType findById(Long id);
}
