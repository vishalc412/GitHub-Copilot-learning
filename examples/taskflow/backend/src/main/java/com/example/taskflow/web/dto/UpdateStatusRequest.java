package com.example.taskflow.web.dto;

import com.example.taskflow.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Payload for a lifecycle transition.
 *
 * @param status the desired next state; must be a legal transition from the
 *               task's current state or the request is rejected with 409
 */
public record UpdateStatusRequest(

        @NotNull(message = "status is required")
        TaskStatus status
) {
}
