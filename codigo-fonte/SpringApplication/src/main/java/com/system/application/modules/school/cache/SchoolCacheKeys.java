package com.system.application.modules.school.cache;

import java.time.Duration;
import java.util.UUID;

public class SchoolCacheKeys {
    private static final String PREFIX = "school::";
    public static final Duration TTL = Duration.ofHours(48);

    private SchoolCacheKeys() {}

    public static String byId(UUID id) {
        return PREFIX + id.toString();
    }

    public static String byOwnerId(UUID id) {
        return PREFIX + id.toString() + "::ownerId";
    }

    public static String byUser(UUID schoolId, UUID userId) {
        return PREFIX + schoolId.toString() + "::" + userId.toString();
    }

    public static String byPatternByUser(UUID schoolId) {
        return PREFIX + schoolId.toString() + "::*";
    }
}
