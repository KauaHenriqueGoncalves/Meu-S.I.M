package com.system.application.modules.licensing.schoolsubscription.validator;

import com.system.application.modules.school.School;

public interface SubscriptionValidator {
    void ensureSchoolHasActiveSubscription(School school);
}
