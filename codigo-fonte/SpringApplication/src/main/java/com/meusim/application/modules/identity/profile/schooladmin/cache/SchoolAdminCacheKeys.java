package com.meusim.application.modules.identity.profile.schooladmin.cache;

import java.time.Duration;
import java.util.UUID;

public final class SchoolAdminCacheKeys {
    private static final String PREFIX = "school-admin::";
    public static final Duration TTL = Duration.ofHours(30);

    private SchoolAdminCacheKeys() {
    }

    public static String page(UUID schoolId, int page, int size, String name) {
        return PREFIX + schoolId + "::" + page + "::" + size + "::" + name + "::page" ;
    }

    public static String byId(UUID schoolId, UUID id) {
        return PREFIX + schoolId + "::" + id + "::byId";
    }

    public static String byUserId(UUID schoolId, UUID userId) {
        return PREFIX + schoolId + "::" + userId + "::byUserId";
    }

    public static String pagePattern(UUID schoolId) {
        return PREFIX + schoolId + "::*::page";
    }
}
