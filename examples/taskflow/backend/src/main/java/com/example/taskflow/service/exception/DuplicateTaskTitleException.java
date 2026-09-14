package com.example.taskflow.service.exception;

/**
 * Raised when creating a task whose title already exists.
 *
 * <p>Mapped to HTTP 409 Conflict. The uniqueness rule lives in the service
 * layer rather than as a database constraint so the lab exercises can show
 * both approaches and their trade-offs.</p>
 */
public class DuplicateTaskTitleException extends RuntimeException {

    private final String title;

    public DuplicateTaskTitleException(String title) {
        super("A task with this title already exists: " + title);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
