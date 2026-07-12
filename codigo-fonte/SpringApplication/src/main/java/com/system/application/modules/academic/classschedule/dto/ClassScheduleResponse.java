package com.system.application.modules.academic.classschedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.system.application.modules.academic.classschedule.ClassSchedule;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

public record ClassScheduleResponse(

        UUID id,
        String weekday,

        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static ClassScheduleResponse of(ClassSchedule c) {
        return new ClassScheduleResponse(
                c.getId(),
                c.getWeekday().getDescription(),
                c.getStartTime(),
                c.getEndTime()
        );
    }
}
