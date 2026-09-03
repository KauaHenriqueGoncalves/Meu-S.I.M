package com.meusim.application.modules.classdiary.lesson.cache;

import java.time.Duration;
import java.util.UUID;

public final class LessonCacheKeys {
    private static final String PREFIX = "lesson::";
    public static final Duration TTL = Duration.ofHours(72);

    private LessonCacheKeys() {
    }

    public static String page(UUID classroomId, int page, int size) {
        return PREFIX + classroomId + "::" + page + "::" + size + "::page";
    }

    public static String agenda(UUID classroomId, int year, int month) {
        return PREFIX + classroomId + "::" + year + "_" + month + "::agenda";
    }

    public static String byId(UUID lessonId) {
        return PREFIX + lessonId + "::byId";
    }

    public static String pagePattern(UUID classroomId) {
        return PREFIX + classroomId + "::*::page";
    }

    public static String agendaPattern(UUID classroomId) {
        return PREFIX + classroomId + "::*::agenda";
    }
}
