package com.example.taskflow.service;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import com.example.taskflow.repository.TaskRepository;
import com.example.taskflow.service.exception.DuplicateTaskTitleException;
import com.example.taskflow.service.exception.IllegalStatusTransitionException;
import com.example.taskflow.service.exception.TaskNotFoundException;
import com.example.taskflow.web.dto.CreateTaskRequest;
import com.example.taskflow.web.dto.TaskStatsResponse;
import com.example.taskflow.web.dto.UpdateTaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TaskService} with a mocked repository.
 *
 * <p>No Spring context: this suite proves the business rules, not the wiring.
 * {@code @DataJpaTest} and the full integration test cover persistence and
 * HTTP respectively.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService")
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    private Task existing;

    @BeforeEach
    void setUp() {
        existing = new Task("Write the runbook", "Cover rollback",
                TaskPriority.MEDIUM, "jordan", LocalDate.now().plusDays(3));
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("persists a new task in the TODO state")
        void persistsNewTask() {
            when(repository.existsByTitleIgnoreCase(anyString())).thenReturn(false);
            when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            CreateTaskRequest request = new CreateTaskRequest(
                    "  Ship the API  ", "  with docs  ",
                    TaskPriority.HIGH, "  priya  ", LocalDate.now().plusDays(1));

            Task created = service.create(request);

            assertThat(created.getStatus()).isEqualTo(TaskStatus.TODO);
            assertThat(created.getPriority()).isEqualTo(TaskPriority.HIGH);
            // Whitespace is normalised on the way in, not left for the UI to handle.
            assertThat(created.getTitle()).isEqualTo("Ship the API");
            assertThat(created.getDescription()).isEqualTo("with docs");
            assertThat(created.getAssignee()).isEqualTo("priya");
        }

        @Test
        @DisplayName("defaults priority to MEDIUM when omitted")
        void defaultsPriority() {
            when(repository.existsByTitleIgnoreCase(anyString())).thenReturn(false);
            when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task created = service.create(new CreateTaskRequest(
                    "No priority given", null, null, null, null));

            assertThat(created.getPriority()).isEqualTo(TaskPriority.MEDIUM);
        }

        @Test
        @DisplayName("normalises blank optional fields to null")
        void blankOptionalsBecomeNull() {
            when(repository.existsByTitleIgnoreCase(anyString())).thenReturn(false);
            when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task created = service.create(new CreateTaskRequest(
                    "Blank optionals", "   ", TaskPriority.LOW, "   ", null));

            assertThat(created.getDescription()).isNull();
            assertThat(created.getAssignee()).isNull();
        }

        @Test
        @DisplayName("rejects a duplicate title without touching the database")
        void rejectsDuplicateTitle() {
            when(repository.existsByTitleIgnoreCase("Ship the API")).thenReturn(true);

            CreateTaskRequest request = new CreateTaskRequest(
                    "Ship the API", null, TaskPriority.LOW, null, null);

            assertThatExceptionOfType(DuplicateTaskTitleException.class)
                    .isThrownBy(() -> service.create(request))
                    .withMessageContaining("Ship the API");

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns the task when it exists")
        void returnsTask() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing));

            assertThat(service.findById(1L)).isSameAs(existing);
        }

        @Test
        @DisplayName("throws TaskNotFoundException when absent")
        void throwsWhenMissing() {
            when(repository.findById(404L)).thenReturn(Optional.empty());

            assertThatExceptionOfType(TaskNotFoundException.class)
                    .isThrownBy(() -> service.findById(404L))
                    .satisfies(ex -> assertThat(ex.getTaskId()).isEqualTo(404L));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("replaces editable fields but not status")
        void replacesEditableFields() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing));
            when(repository.existsByTitleIgnoreCase("Rewrite the runbook")).thenReturn(false);

            Task updated = service.update(1L, new UpdateTaskRequest(
                    "Rewrite the runbook", "Now with alerts",
                    TaskPriority.URGENT, "sam", LocalDate.now().plusDays(9)));

            assertThat(updated.getTitle()).isEqualTo("Rewrite the runbook");
            assertThat(updated.getPriority()).isEqualTo(TaskPriority.URGENT);
            assertThat(updated.getAssignee()).isEqualTo("sam");
            // PUT must not become a back door around the lifecycle rules.
            assertThat(updated.getStatus()).isEqualTo(TaskStatus.TODO);
        }

        @Test
        @DisplayName("allows keeping the same title (case-insensitively)")
        void allowsUnchangedTitle() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing));

            Task updated = service.update(1L, new UpdateTaskRequest(
                    "write the runbook", null, TaskPriority.LOW, null, null));

            assertThat(updated.getTitle()).isEqualTo("write the runbook");
            // No uniqueness check should fire for an unchanged title.
            verify(repository, never()).existsByTitleIgnoreCase(anyString());
        }

        @Test
        @DisplayName("rejects renaming onto another task's title")
        void rejectsRenameCollision() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing));
            when(repository.existsByTitleIgnoreCase("Taken title")).thenReturn(true);

            UpdateTaskRequest request = new UpdateTaskRequest(
                    "Taken title", null, TaskPriority.LOW, null, null);

            assertThatExceptionOfType(DuplicateTaskTitleException.class)
                    .isThrownBy(() -> service.update(1L, request));
        }
    }

    @Nested
    @DisplayName("changeStatus")
    class ChangeStatus {

        @Test
        @DisplayName("applies a legal transition")
        void appliesLegalTransition() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing));

            Task updated = service.changeStatus(1L, TaskStatus.IN_PROGRESS);

            assertThat(updated.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("translates a domain violation into IllegalStatusTransitionException")
        void rejectsIllegalTransition() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing));

            assertThatExceptionOfType(IllegalStatusTransitionException.class)
                    .isThrownBy(() -> service.changeStatus(1L, TaskStatus.DONE))
                    .satisfies(ex -> {
                        assertThat(ex.getFrom()).isEqualTo(TaskStatus.TODO);
                        assertThat(ex.getTo()).isEqualTo(TaskStatus.DONE);
                    });
        }

        @Test
        @DisplayName("is a no-op when already in the target state")
        void sameStateIsNoOp() {
            when(repository.findById(1L)).thenReturn(Optional.of(existing));

            Task updated = service.changeStatus(1L, TaskStatus.TODO);

            assertThat(updated.getStatus()).isEqualTo(TaskStatus.TODO);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes an existing task")
        void deletesExisting() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
            verify(repository).deleteById(captor.capture());
            assertThat(captor.getValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("throws rather than silently ignoring a missing task")
        void throwsWhenMissing() {
            when(repository.existsById(404L)).thenReturn(false);

            assertThatExceptionOfType(TaskNotFoundException.class)
                    .isThrownBy(() -> service.delete(404L));

            verify(repository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("stats")
    class Stats {

        @Test
        @DisplayName("computes the completion rate")
        void computesCompletionRate() {
            when(repository.countByStatus(TaskStatus.TODO)).thenReturn(2L);
            when(repository.countByStatus(TaskStatus.IN_PROGRESS)).thenReturn(3L);
            when(repository.countByStatus(TaskStatus.DONE)).thenReturn(5L);

            TaskStatsResponse stats = service.stats();

            assertThat(stats.total()).isEqualTo(10L);
            assertThat(stats.done()).isEqualTo(5L);
            assertThat(stats.completionRate()).isEqualTo(0.5);
        }

        @Test
        @DisplayName("reports a zero rate for an empty board instead of dividing by zero")
        void handlesEmptyBoard() {
            when(repository.countByStatus(any(TaskStatus.class))).thenReturn(0L);

            TaskStatsResponse stats = service.stats();

            assertThat(stats.total()).isZero();
            assertThat(stats.completionRate()).isZero();
        }
    }
}
