package com.system.application.modules.identity.systemadmin.cache;

import java.time.Duration;
import java.util.UUID;

public final class SystemAdminCacheKeys {
    private static final String PREFIX = "system-admin::";
    public static final Duration TTL = Duration.ofHours(30);

    private SystemAdminCacheKeys() {}

    public static String all() {
        return PREFIX + "findAll";
    }

    public static String byId(UUID id) {
        return PREFIX + id.toString();
    }
}
