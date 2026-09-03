package com.meusim.application.modules.classdiary.lesson.enums;

public enum LessonDisplayStatus {
    DONE("done"),
    CANCELED("canceled"),
    LATE("late"),
    PENDING("pending");

    private final String name;

    LessonDisplayStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
