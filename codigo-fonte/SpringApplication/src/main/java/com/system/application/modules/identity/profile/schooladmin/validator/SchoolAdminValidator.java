package com.system.application.modules.identity.profile.schooladmin.validator;

import com.system.application.modules.licensing.schoolsubscription.SchoolSubscription;
import com.system.application.modules.school.School;

public interface SchoolAdminValidator {
    void ensureSubscriptionSupportsSchoolAdminCound(School school, SchoolSubscription sub);
    void ensureSchoolAdminAndOwnerBelongsSameSchool(School ownerSchool, School admin);
    void ensureSchoolHasSubscription(School school);
}
