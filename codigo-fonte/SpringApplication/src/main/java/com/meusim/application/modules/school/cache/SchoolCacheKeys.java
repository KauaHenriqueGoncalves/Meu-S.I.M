package com.meusim.application.modules.school.cache;

import java.time.Duration;
import java.util.UUID;

public class SchoolCacheKeys {
    private static final String PREFIX = "school::";
    public static final Duration TTL = Duration.ofHours(48);

    private SchoolCacheKeys() {
    }

    public static String byId(UUID id) {
        return PREFIX + id + "::byId";
    }

    public static String byOwnerId(UUID id) {
        return PREFIX + id + "::byOwnerId";
    }

    public static String byUserId(UUID schoolId, UUID userId) {
        return PREFIX + schoolId + "::" + userId + "::byUserId";
    }

    public static String byPatternByUserId(UUID schoolId) {
        return PREFIX + schoolId + "::*";
    }
}
