package com.meusim.application.modules.classdiary.lesson.enums;

public enum LessonStatus {
    DONE("done"),
    CANCELED("canceled");

    private final String name;

    LessonStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
