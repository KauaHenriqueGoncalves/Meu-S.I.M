package com.meusim.application.modules.classdiary.attendance.enums;

public enum AttendanceStatus {
    PRESENT("present"),
    ABSENT("absent"),
    JUSTIFIED("justified");

    private final String name;

    AttendanceStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
