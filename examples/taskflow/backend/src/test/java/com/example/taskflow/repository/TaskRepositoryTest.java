package com.example.taskflow.repository;

import com.example.taskflow.config.JpaAuditingConfig;
import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persistence-layer tests against H2.
 *
 * <p>{@code @DataJpaTest} starts only the JPA slice — no web layer — and rolls
 * back after each test, so these run in about a second while still exercising
 * real SQL generation. That matters for {@link TaskSpecifications}: a
 * Specification bug is invisible to a mocked repository.</p>
 *
 * <p>{@code @Import(JpaAuditingConfig.class)} is required: slice tests do not
 * component-scan arbitrary {@code @Configuration} classes, so without it
 * auditing never runs and {@code createdAt} violates its NOT NULL constraint.</p>
 */
@DataJpaTest
@Import(JpaAuditingConfig.class)
@DisplayName("TaskRepository + specifications")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void seedFixtures() {
        LocalDate today = LocalDate.now();

        Task overdue = new Task("Overdue security patch", "Rotate the signing keys",
                TaskPriority.URGENT, "Priya", today.minusDays(5));

        Task inProgress = new Task("Refactor billing module", "Extract the pricing engine",
                TaskPriority.HIGH, "sam", today.plusDays(2));
        inProgress.transitionTo(TaskStatus.IN_PROGRESS);

        Task done = new Task("Archive legacy logs", "Move to cold storage",
                TaskPriority.LOW, "jordan", today.minusDays(10));
        done.transitionTo(TaskStatus.IN_PROGRESS);
        done.transitionTo(TaskStatus.DONE);

        Task unassigned = new Task("Investigate flaky test", null,
                TaskPriority.MEDIUM, null, null);

        repository.saveAll(java.util.List.of(overdue, inProgress, done, unassigned));
        repository.flush();
    }

    @Test
    @DisplayName("populates audit timestamps and the version on save")
    void populatesAuditFields() {
        Task saved = repository.findAll().getFirst();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getVersion()).isNotNull();
    }

    @Test
    @DisplayName("countByStatus counts only the requested state")
    void countsByStatus() {
        assertThat(repository.countByStatus(TaskStatus.TODO)).isEqualTo(2);
        assertThat(repository.countByStatus(TaskStatus.IN_PROGRESS)).isEqualTo(1);
        assertThat(repository.countByStatus(TaskStatus.DONE)).isEqualTo(1);
    }

    @Test
    @DisplayName("existsByTitleIgnoreCase ignores case")
    void titleCheckIgnoresCase() {
        assertThat(repository.existsByTitleIgnoreCase("OVERDUE SECURITY PATCH")).isTrue();
        assertThat(repository.existsByTitleIgnoreCase("nothing like this")).isFalse();
    }

    @Test
    @DisplayName("filters by status")
    void filtersByStatus() {
        Page<Task> page = repository.findAll(
                TaskSpecifications.hasStatus(TaskStatus.IN_PROGRESS),
                PageRequest.of(0, 10));

        assertThat(page.getContent())
                .singleElement()
                .extracting(Task::getTitle)
                .isEqualTo("Refactor billing module");
    }

    @Test
    @DisplayName("filters by assignee case-insensitively")
    void filtersByAssigneeIgnoringCase() {
        Page<Task> page = repository.findAll(
                TaskSpecifications.hasAssignee("priya"),
                PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().getAssignee()).isEqualTo("Priya");
    }

    @Test
    @DisplayName("free-text search matches title or description, and survives a null description")
    void searchesTitleAndDescription() {
        Page<Task> byTitle = repository.findAll(
                TaskSpecifications.matchesText("billing"), PageRequest.of(0, 10));
        assertThat(byTitle.getTotalElements()).isEqualTo(1);

        Page<Task> byDescription = repository.findAll(
                TaskSpecifications.matchesText("cold storage"), PageRequest.of(0, 10));
        assertThat(byDescription.getTotalElements()).isEqualTo(1);

        // "Investigate flaky test" has a null description — COALESCE must keep it
        // out of the results rather than blowing up the query.
        Page<Task> noMatch = repository.findAll(
                TaskSpecifications.matchesText("nonexistent-term"), PageRequest.of(0, 10));
        assertThat(noMatch).isEmpty();
    }

    @Test
    @DisplayName("overdue filter excludes DONE tasks even when past due")
    void overdueExcludesDone() {
        Page<Task> page = repository.findAll(
                TaskSpecifications.isOverdue(true), PageRequest.of(0, 10));

        // "Archive legacy logs" is 10 days past due but DONE, so it must not appear.
        assertThat(page.getContent())
                .singleElement()
                .extracting(Task::getTitle)
                .isEqualTo("Overdue security patch");
    }

    @Test
    @DisplayName("chained specifications AND together")
    void chainsSpecifications() {
        Specification<Task> spec = TaskSpecifications.hasStatus(TaskStatus.TODO)
                .and(TaskSpecifications.hasPriority(TaskPriority.URGENT))
                .and(TaskSpecifications.matchesText("patch"));

        Page<Task> page = repository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("unrestricted specification returns everything")
    void unrestrictedReturnsAll() {
        Page<Task> page = repository.findAll(
                TaskSpecifications.unrestricted(), PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(4);
    }

    @Test
    @DisplayName("paginates and sorts")
    void paginatesAndSorts() {
        Page<Task> firstPage = repository.findAll(
                TaskSpecifications.unrestricted(),
                PageRequest.of(0, 2, Sort.by("title").ascending()));

        assertThat(firstPage.getTotalElements()).isEqualTo(4);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.getContent())
                .extracting(Task::getTitle)
                .containsExactly("Archive legacy logs", "Investigate flaky test");
    }
}
