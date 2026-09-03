package com.meusim.application.modules.academic.classtype.service;

import com.meusim.application.modules.academic.classtype.ClassType;

import java.util.List;

public interface ClassTypeService {
    List<ClassType> findAll();
    ClassType findById(Long id);
}
