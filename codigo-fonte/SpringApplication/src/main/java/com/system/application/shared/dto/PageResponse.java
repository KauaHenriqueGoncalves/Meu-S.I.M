package com.system.application.shared.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        int totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public PageResponse {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Informe valores positivos ou igual à 0!");
        }
    }
}
