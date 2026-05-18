package com.system.application.shared.services.cache.keys;

import java.util.UUID;

public final class CacheKeys {
    private CacheKeys() { }

    public static String role(String nameRole) {
        return "role::" + nameRole;
    }

    public static String legalGuardianPattern(UUID id) {
        return "legalGuardian::" + id.toString() + "*";
    }

    public static String legalGuardian(UUID legalGuardianId, String suffix) {
        return "legalGuardian::" + legalGuardianId.toString() + "::" + suffix;
    }

    public static String legalGuardian(UUID id, int page, int size, String name) {
        return "legalGuardian::" +
                id.toString() + "::" +
                page + "::" +
                size + "::" +
                ((name != null) ? name.trim() : "") + "::page";
    }

    public static String collaboratorPattern(UUID id) {
        return "collaborator::" + id.toString() + "*";
    }

    public static String collaborator(UUID collaboratorId, String suffix) {
        return "collaborator::" + collaboratorId.toString() + "::" + suffix;
    }

    public static String collaborator(UUID id, int page, int size, String name) {
        return "collaborator::" +
                id.toString() + "::" +
                page + "::" +
                size + "::" +
                ((name != null) ? name.trim() : "") + "::page";
    }

    public static String subjectPattern(UUID id) {
        return "subject::" + id.toString() + "*";
    }

    public static String subject(UUID id, int page, int size) {
        return "subject::" +
                id.toString() + "::" +
                page + "::" +
                size + "::page";
    }

    public static String studentPattern(UUID id) {
        return "student::" + id.toString() + "*";
    }

    public static String student(UUID studentId, String suffix) {
        return "student::" + studentId.toString() + "::" + suffix;
    }

    public static String student(UUID id, int page, int size, String name) {
        return "student::" +
                id.toString() + "::" +
                page + "::" +
                size + "::" +
                ((name != null) ? name.trim() : "") + "::page";
    }
}
