package com.system.application.modules.identity.user.cache;

import java.time.Duration;
import java.util.UUID;

public final class UserCacheKeys {
    private static final String PREFIX = "user::";
    public static final Duration TTL = Duration.ofHours(24);

    private UserCacheKeys() {}

    public static String me(UUID id) {
        return PREFIX + id.toString() + "me";
    }
}
