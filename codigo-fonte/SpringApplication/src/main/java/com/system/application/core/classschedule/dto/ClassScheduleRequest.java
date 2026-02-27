package com.system.application.core.classschedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.system.application.core.classschedule.enums.Weekday;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

public record ClassScheduleRequest(
        @NotNull(message = "Informe o dia da semana")
        Weekday weekday,

        @NotNull(message = "Informe o horário de início")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "Informe o horário de fim")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @AssertTrue(message = "O horário de início deve ser antes do horário de fim")
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) {
            return true; // deixa o @NotNull cuidar disso
        }
        return startTime.isBefore(endTime);
    }
}
