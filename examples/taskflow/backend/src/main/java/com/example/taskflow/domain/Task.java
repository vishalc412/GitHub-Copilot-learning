package com.example.taskflow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A unit of work tracked by TaskFlow.
 *
 * <p>Deliberately written without Lombok: this project uses Java records for
 * DTOs and explicit accessors for JPA entities, so there is no annotation
 * processor in the build and nothing hidden from a reader (or from Copilot)
 * at the point of use.</p>
 *
 * <p>Timestamps are managed by Spring Data JPA auditing — see
 * {@code @EnableJpaAuditing} on the application class. {@link #version} gives
 * optimistic locking, so two concurrent edits to the same task surface as a
 * conflict instead of a silent lost update.</p>
 */
@Entity
@Table(
        name = "tasks",
        indexes = {
                @Index(name = "idx_task_status", columnList = "status"),
                @Index(name = "idx_task_assignee", columnList = "assignee"),
                @Index(name = "idx_task_due_date", columnList = "due_date")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class Task {

    public static final int TITLE_MAX = 140;
    public static final int DESCRIPTION_MAX = 2_000;
    public static final int ASSIGNEE_MAX = 80;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = TITLE_MAX)
    private String title;

    @Column(length = DESCRIPTION_MAX)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Column(length = ASSIGNEE_MAX)
    private String assignee;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    /** Required by JPA. */
    protected Task() {
    }

    /**
     * Creates a new task in the {@link TaskStatus#TODO} state.
     *
     * @param title       short summary, required
     * @param description longer detail, may be {@code null}
     * @param priority    urgency; defaults to {@link TaskPriority#MEDIUM} when {@code null}
     * @param assignee    person responsible, may be {@code null}
     * @param dueDate     target completion date, may be {@code null}
     */
    public Task(String title,
                String description,
                TaskPriority priority,
                String assignee,
                LocalDate dueDate) {
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.description = description;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.assignee = assignee;
        this.dueDate = dueDate;
        this.status = TaskStatus.TODO;
    }

    /**
     * Moves this task to {@code target}, enforcing the lifecycle rules declared
     * on {@link TaskStatus}.
     *
     * @param target the desired next state
     * @throws IllegalStateException if the transition is not permitted
     */
    public void transitionTo(TaskStatus target) {
        Objects.requireNonNull(target, "target status must not be null");
        if (this.status == target) {
            return;
        }
        if (!this.status.canTransitionTo(target)) {
            throw new IllegalStateException(
                    "Illegal status transition: %s -> %s".formatted(this.status, target));
        }
        this.status = target;
    }

    /**
     * @return {@code true} when the task has a due date in the past and is not yet done
     */
    public boolean isOverdue() {
        return dueDate != null
                && status != TaskStatus.DONE
                && dueDate.isBefore(LocalDate.now());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "title must not be null");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = Objects.requireNonNull(priority, "priority must not be null");
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Task task)) {
            return false;
        }
        // Identity comparison only once persisted; transient instances are never equal.
        return id != null && id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "Task{id=%d, title='%s', status=%s, priority=%s}"
                .formatted(id, title, status, priority);
    }
}
