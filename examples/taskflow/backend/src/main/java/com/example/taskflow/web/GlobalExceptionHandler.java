package com.example.taskflow.web;

import com.example.taskflow.service.exception.DuplicateTaskTitleException;
import com.example.taskflow.service.exception.IllegalStatusTransitionException;
import com.example.taskflow.service.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translates exceptions into RFC 9457 {@code application/problem+json} responses.
 *
 * <p>Uses Spring's built-in {@link ProblemDetail} rather than a bespoke error
 * record, so every error in the API shares one machine-readable shape:</p>
 *
 * <pre>{@code
 * {
 *   "type": "https://taskflow.example/problems/task-not-found",
 *   "title": "Task not found",
 *   "status": 404,
 *   "detail": "Task not found: 42",
 *   "instance": "/api/v1/tasks/42",
 *   "timestamp": "2026-09-14T10:15:30Z"
 * }
 * }</pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PROBLEM_BASE = "https://taskflow.example/problems/";

    /**
     * Handles a request for a task that does not exist.
     *
     * @param ex the raised exception
     * @return 404 Not Found
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ProblemDetail handleNotFound(TaskNotFoundException ex) {
        log.debug("Task not found: {}", ex.getTaskId());
        return problem(HttpStatus.NOT_FOUND, "Task not found", ex.getMessage(), "task-not-found");
    }

    /**
     * Handles an attempt to make an illegal lifecycle transition.
     *
     * @param ex the raised exception
     * @return 409 Conflict, including the attempted transition as properties
     */
    @ExceptionHandler(IllegalStatusTransitionException.class)
    public ProblemDetail handleIllegalTransition(IllegalStatusTransitionException ex) {
        ProblemDetail detail = problem(HttpStatus.CONFLICT,
                "Illegal status transition", ex.getMessage(), "illegal-status-transition");
        detail.setProperty("from", ex.getFrom());
        detail.setProperty("to", ex.getTo());
        return detail;
    }

    /**
     * Handles creating or renaming a task onto an existing title.
     *
     * @param ex the raised exception
     * @return 409 Conflict
     */
    @ExceptionHandler(DuplicateTaskTitleException.class)
    public ProblemDetail handleDuplicateTitle(DuplicateTaskTitleException ex) {
        ProblemDetail detail = problem(HttpStatus.CONFLICT,
                "Duplicate task title", ex.getMessage(), "duplicate-task-title");
        detail.setProperty("conflictingTitle", ex.getTitle());
        return detail;
    }

    /**
     * Handles bean-validation failures on request bodies, reporting every
     * offending field rather than only the first.
     *
     * @param ex the raised exception
     * @return 400 Bad Request with a {@code errors} map of field → message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.merge(error.getField(),
                        error.getDefaultMessage() == null ? "invalid" : error.getDefaultMessage(),
                        (first, second) -> first + "; " + second));

        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more fields are invalid",
                "validation-failed");
        detail.setProperty("errors", errors);
        return detail;
    }

    /**
     * Handles an unparseable path variable or query parameter — for example
     * {@code ?status=NOPE} where an enum was expected.
     *
     * @param ex the raised exception
     * @return 400 Bad Request naming the offending parameter
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String required = ex.getRequiredType() == null
                ? "the expected type"
                : ex.getRequiredType().getSimpleName();
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST,
                "Invalid parameter",
                "'%s' is not a valid value for %s".formatted(ex.getValue(), required),
                "invalid-parameter");
        detail.setProperty("parameter", ex.getName());
        return detail;
    }

    /**
     * Handles a concurrent modification detected by optimistic locking.
     *
     * @param ex the raised exception
     * @return 409 Conflict
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        log.warn("Optimistic locking conflict: {}", ex.getMessage());
        return problem(HttpStatus.CONFLICT,
                "Concurrent modification",
                "This task was modified by someone else. Reload and try again.",
                "concurrent-modification");
    }

    /**
     * Handles a request for a URL that maps to nothing.
     *
     * <p>This handler exists because of the catch-all below. Spring raises
     * {@link NoResourceFoundException} for an unmatched path, and without an
     * explicit mapping the {@code Exception} handler swallows it and reports a
     * 500 — turning "you typed the wrong URL" into "the server is broken", and
     * logging a stack trace for every stray scanner request. Any blanket
     * exception handler needs this companion.</p>
     *
     * @param ex the raised exception
     * @return 404 Not Found
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResource(NoResourceFoundException ex) {
        log.debug("No handler for path: {}", ex.getResourcePath());
        return problem(HttpStatus.NOT_FOUND,
                "Resource not found",
                "No endpoint matches this path.",
                "no-such-endpoint");
    }

    /**
     * Last-resort handler. Logs the full stack trace but never leaks internals
     * to the client.
     *
     * @param ex the raised exception
     * @return 500 Internal Server Error with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "An unexpected error occurred. Please contact support if it persists.",
                "internal-error");
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail, String slug) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create(PROBLEM_BASE + slug));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
