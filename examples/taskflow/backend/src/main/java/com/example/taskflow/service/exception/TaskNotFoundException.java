package com.example.taskflow.service.exception;

/**
 * Raised when a task is requested by an id that does not exist.
 *
 * <p>Unchecked by design: a missing task is a caller error, not a recoverable
 * condition every method signature should advertise. Mapped to HTTP 404 by
 * {@code GlobalExceptionHandler}.</p>
 */
public class TaskNotFoundException extends RuntimeException {

    private final Long taskId;

    public TaskNotFoundException(Long taskId) {
        super("Task not found: " + taskId);
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }
}
