package com.meusim.application.modules.identity.profile.schooladmin.validator;

import com.meusim.application.modules.school.School;

public interface SchoolAdminValidator {
    void ensureSubscriptionSupportsSchoolAdminCount(School school);
    void ensureSchoolAdminAndOwnerBelongsSameSchool(School ownerSchool, School admin);
    void ensureSchoolHasSubscription(School school);
}
