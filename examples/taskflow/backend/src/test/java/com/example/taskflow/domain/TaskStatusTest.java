package com.example.taskflow.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pure unit tests for the lifecycle rules.
 *
 * <p>No Spring context, no database — the rule lives on the enum, so the test
 * runs in microseconds. Fast tests like this are where the transition matrix
 * belongs; the integration test only needs to prove the rule is wired up.</p>
 */
@DisplayName("TaskStatus lifecycle")
class TaskStatusTest {

    @Nested
    @DisplayName("permitted transitions")
    class Permitted {

        @ParameterizedTest(name = "{0} -> {1} is allowed")
        @CsvSource({
                "TODO, IN_PROGRESS",
                "IN_PROGRESS, TODO",
                "IN_PROGRESS, DONE"
        })
        void allowsLegalTransitions(TaskStatus from, TaskStatus to) {
            assertThat(from.canTransitionTo(to)).isTrue();
        }
    }

    @Nested
    @DisplayName("forbidden transitions")
    class Forbidden {

        @ParameterizedTest(name = "{0} -> {1} is rejected")
        @CsvSource({
                "TODO, DONE",
                "DONE, TODO",
                "DONE, IN_PROGRESS"
        })
        void rejectsIllegalTransitions(TaskStatus from, TaskStatus to) {
            assertThat(from.canTransitionTo(to)).isFalse();
        }

        @Test
        @DisplayName("DONE is terminal")
        void doneIsTerminal() {
            assertThat(TaskStatus.DONE.allowedTransitions()).isEmpty();
        }
    }

    @ParameterizedTest
    @EnumSource(TaskStatus.class)
    @DisplayName("no state lists itself as a transition")
    void noSelfTransitions(TaskStatus status) {
        assertThat(status.allowedTransitions()).doesNotContain(status);
    }
}
