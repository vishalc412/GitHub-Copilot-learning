package com.example.taskflow.service.exception;

import com.example.taskflow.domain.TaskStatus;

/**
 * Raised when a caller attempts a status change the task lifecycle forbids.
 *
 * <p>Mapped to HTTP 409 Conflict — the request was well-formed, but the
 * resource is not in a state that permits it.</p>
 */
public class IllegalStatusTransitionException extends RuntimeException {

    private final TaskStatus from;
    private final TaskStatus to;

    public IllegalStatusTransitionException(TaskStatus from, TaskStatus to) {
        super("Illegal status transition: %s -> %s".formatted(from, to));
        this.from = from;
        this.to = to;
    }

    public TaskStatus getFrom() {
        return from;
    }

    public TaskStatus getTo() {
        return to;
    }
}
