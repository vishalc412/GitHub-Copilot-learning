package com.example.taskflow.web.dto;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Payload for creating a task.
 *
 * <p>A record, not a Lombok class: immutable, no annotation processor, and the
 * full shape is visible in one line — which is also exactly the context
 * Copilot needs to generate correct call sites.</p>
 *
 * @param title       short summary; required, 3–140 characters
 * @param description longer detail; optional, up to 2000 characters
 * @param priority    urgency; defaults to {@code MEDIUM} when omitted
 * @param assignee    person responsible; optional
 * @param dueDate     target completion date; optional, may be in the past
 *                    (back-dated imports are legitimate)
 */
public record CreateTaskRequest(

        @NotBlank(message = "title is required")
        @Size(min = 3, max = Task.TITLE_MAX,
                message = "title must be between 3 and " + Task.TITLE_MAX + " characters")
        String title,

        @Size(max = Task.DESCRIPTION_MAX,
                message = "description must not exceed " + Task.DESCRIPTION_MAX + " characters")
        String description,

        TaskPriority priority,

        @Size(max = Task.ASSIGNEE_MAX,
                message = "assignee must not exceed " + Task.ASSIGNEE_MAX + " characters")
        String assignee,

        LocalDate dueDate
) {
}
