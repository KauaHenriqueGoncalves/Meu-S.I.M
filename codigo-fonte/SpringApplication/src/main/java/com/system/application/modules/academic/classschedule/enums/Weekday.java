package com.system.application.modules.academic.classschedule.enums;

public enum Weekday {
    MONDAY(1, "segunda-feira"),
    TUESDAY(2, "terça-feira"),
    WEDNESDAY(3, "quarta-feira"),
    THURSDAY(4, "quinta-feira"),
    FRIDAY(5, "sexta-feira"),
    SATURDAY(6, "sábado"),
    SUNDAY(7, "domingo");

    private final int order;
    private final String description;

    Weekday(int order, String description) {
        this.order = order;
        this.description = description;
    }

    public int getOrder() {
        return order;
    }

    public String getDescription() {
        return description;
    }
}
