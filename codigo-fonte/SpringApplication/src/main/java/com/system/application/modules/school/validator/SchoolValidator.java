package com.system.application.modules.school.validator;

import com.system.application.modules.school.School;
import java.util.UUID;

public interface SchoolValidator {
    void ensureSchoolSameByOwnerId(School userSchool, School ownerSchool);
    void ensureSchoolSameByOwnerId(UUID userSchoolId, School ownerSchool);
    void ensureSchoolAlreadyExistNameCode(String nameCode);
    void ensureSchoolAlreadyExistCnpj(String cnpj);
}
