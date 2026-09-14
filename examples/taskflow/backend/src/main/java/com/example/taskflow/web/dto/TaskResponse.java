package com.example.taskflow.web.dto;

import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

/**
 * API representation of a task.
 *
 * <p>{@code overdue} and {@code allowedTransitions} are computed server-side
 * so the frontend never has to re-implement domain rules — the UI can render
 * exactly the buttons the backend would accept.</p>
 *
 * @param id                 database identifier
 * @param title              short summary
 * @param description        longer detail, may be {@code null}
 * @param status             current lifecycle state
 * @param priority           urgency
 * @param assignee           person responsible, may be {@code null}
 * @param dueDate            target completion date, may be {@code null}
 * @param overdue            {@code true} when past due and not done
 * @param allowedTransitions states this task may legally move to next
 * @param createdAt          creation timestamp
 * @param updatedAt          last modification timestamp
 * @param version            optimistic-locking version
 */
public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        String assignee,
        LocalDate dueDate,
        boolean overdue,
        java.util.List<TaskStatus> allowedTransitions,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {
}
