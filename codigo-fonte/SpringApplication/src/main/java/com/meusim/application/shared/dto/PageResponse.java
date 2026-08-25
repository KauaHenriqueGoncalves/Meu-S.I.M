package com.meusim.application.shared.dto;

import org.springframework.data.domain.Page;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

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

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public <R> PageResponse<R> map(Function<T, R> mapper) {
        return new PageResponse<>(
                content.stream().map(mapper).toList(),
                page,
                size,
                totalPages,
                totalElements,
                hasNext,
                hasPrevious
        );
    }
}
