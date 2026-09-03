package com.meusim.application.modules.licensing.schoolsubscription.validator;

import com.meusim.application.modules.school.School;

public interface SubscriptionValidator {
    void ensureSchoolHasActiveSubscription(School school);
    void ensureSubscriptionSupportsSchoolAdminCount(School school, int current);
}
