package com.example.taskflow.domain;

import java.util.Set;

/**
 * Lifecycle state of a {@link Task}.
 *
 * <p>Legal transitions are encoded here rather than scattered through the
 * service layer, so the rule has exactly one home:</p>
 *
 * <pre>
 *   TODO ──▶ IN_PROGRESS ──▶ DONE
 *     ▲            │           │
 *     └────────────┘           │  (DONE is terminal)
 * </pre>
 */
public enum TaskStatus {

    TODO,
    IN_PROGRESS,
    DONE;

    /**
     * Returns the states this state may legally move to.
     *
     * @return the set of permitted next states (empty for terminal states)
     */
    public Set<TaskStatus> allowedTransitions() {
        return switch (this) {
            case TODO -> Set.of(IN_PROGRESS);
            case IN_PROGRESS -> Set.of(TODO, DONE);
            case DONE -> Set.of();
        };
    }

    /**
     * Tests whether a move from this state to {@code target} is legal.
     *
     * @param target the desired next state
     * @return {@code true} if the transition is permitted
     */
    public boolean canTransitionTo(TaskStatus target) {
        return allowedTransitions().contains(target);
    }
}
