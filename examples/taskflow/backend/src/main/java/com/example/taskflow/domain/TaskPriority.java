package com.example.taskflow.domain;

/**
 * Relative urgency of a {@link Task}, ordered lowest to highest.
 *
 * <p>Declaration order is meaningful: {@code ordinal()} is used for sorting,
 * so new values must be inserted in the correct position, never appended
 * blindly.</p>
 */
public enum TaskPriority {

    LOW,
    MEDIUM,
    HIGH,
    URGENT;

    /**
     * @return {@code true} if this priority should surface in an escalation view
     */
    public boolean isEscalated() {
        return this == HIGH || this == URGENT;
    }
}
