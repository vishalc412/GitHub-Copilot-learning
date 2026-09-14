package com.example.taskflow.web.dto;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Payload for a full replacement update (HTTP PUT semantics).
 *
 * <p>Status is deliberately absent: lifecycle changes go through
 * {@code PATCH /api/v1/tasks/{id}/status} so the transition rules in
 * {@code TaskStatus} cannot be bypassed by a blanket field overwrite.</p>
 *
 * @param title       short summary; required, 3–140 characters
 * @param description longer detail; optional
 * @param priority    urgency; required on update (PUT replaces the whole resource)
 * @param assignee    person responsible; optional
 * @param dueDate     target completion date; optional
 */
public record UpdateTaskRequest(

        @NotBlank(message = "title is required")
        @Size(min = 3, max = Task.TITLE_MAX,
                message = "title must be between 3 and " + Task.TITLE_MAX + " characters")
        String title,

        @Size(max = Task.DESCRIPTION_MAX,
                message = "description must not exceed " + Task.DESCRIPTION_MAX + " characters")
        String description,

        @NotNull(message = "priority is required")
        TaskPriority priority,

        @Size(max = Task.ASSIGNEE_MAX,
                message = "assignee must not exceed " + Task.ASSIGNEE_MAX + " characters")
        String assignee,

        LocalDate dueDate
) {
}
