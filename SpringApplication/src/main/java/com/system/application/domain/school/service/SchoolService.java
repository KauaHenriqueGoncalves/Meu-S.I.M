package com.system.application.domain.school.service;

import com.system.application.domain.school.School;

import java.util.UUID;

public interface SchoolService {
    School findById(UUID id);
    School save(School school);
}
