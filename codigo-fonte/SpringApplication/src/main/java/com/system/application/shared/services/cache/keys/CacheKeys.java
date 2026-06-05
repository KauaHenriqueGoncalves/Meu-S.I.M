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

    public static String legalGuardian(UUID id, String suffix) {
        return "legalGuardian::" + id.toString() + "::" + suffix;
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

    public static String collaborator(UUID id, String suffix) {
        return "collaborator::" + id.toString() + "::" + suffix;
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

    public static String student(UUID id, String suffix) {
        return "student::" + id.toString() + "::" + suffix;
    }

    public static String student(UUID id, int page, int size, String name) {
        return "student::" +
                id.toString() + "::" +
                page + "::" +
                size + "::" +
                ((name != null) ? name.trim() : "") + "::page";
    }

    public static String classTypeList() {
        return "classType::List";
    }

    public static String classType(long id) {
        return "classType::" + id;
    }

    public static String classroomPattern(UUID id) {
        return "classroom::" + id.toString() + "*";
    }

    public static String classroom(UUID id, int page, int size) {
        return "classroom::" +
                id.toString() + "::" +
                page + "::" +
                size + "::page";
    }

    public static String classroom(UUID id, String suffix) {
        return "classroom::" + id.toString() + "::" + suffix;
    }

    public static String classSchedulePattern(UUID id) {
        return "classSchedule::" + id.toString() + "*";
    }

    public static String classSchedule(UUID schoolId, UUID classScheduleId, String suffix) {
        return "classSchedule::" + schoolId.toString() + "::" + classScheduleId.toString() + "::" + suffix;
    }

    public static String billingDiscountPattern() {
        return "billingDiscount::*";
    }

    public static String billingDiscount(String suffix) {
        return "billingDiscount::" + suffix;
    }

    public static String schoolPlanPattern() {
        return "schoolPlan::*";
    }

    public static String schoolPlan(String suffix) {
        return "schoolPlan::" + suffix;
    }

    public static String subscriptionPattern(UUID id) {
        return "subscription::" + id.toString() + "*";
    }

    public static String subscription(UUID id, String suffix) {
        return "subscription::" + id.toString() + "::" + suffix;
    }

    public static String subscription(UUID id, int page, int size) {
        return "subscription::" + id.toString() + "::" + page + "::" + size + "::page";
    }

    public static String school(UUID id, String suffix) {
        return "school::" + id.toString() + "::" + suffix;
    }
}
