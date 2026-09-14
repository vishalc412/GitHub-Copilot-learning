package com.example.taskflow.web.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * A stable, serialization-friendly page envelope.
 *
 * <p>Spring's {@code Page} serializes with an unstable internal shape and logs
 * a warning when returned directly from a controller, so the API exposes this
 * explicit contract instead.</p>
 *
 * @param content       the items on this page
 * @param page          zero-based page number
 * @param size          requested page size
 * @param totalElements total matching items across all pages
 * @param totalPages    total number of pages
 * @param first         {@code true} when this is the first page
 * @param last          {@code true} when this is the last page
 * @param <T>           item type
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    /**
     * Converts a Spring {@link Page} of entities into a page of DTOs.
     *
     * @param source the source page
     * @param mapper entity-to-DTO conversion
     * @param <E>    entity type
     * @param <D>    DTO type
     * @return the mapped page envelope
     */
    public static <E, D> PageResponse<D> from(Page<E> source, Function<E, D> mapper) {
        return new PageResponse<>(
                source.getContent().stream().map(mapper).toList(),
                source.getNumber(),
                source.getSize(),
                source.getTotalElements(),
                source.getTotalPages(),
                source.isFirst(),
                source.isLast());
    }
}
