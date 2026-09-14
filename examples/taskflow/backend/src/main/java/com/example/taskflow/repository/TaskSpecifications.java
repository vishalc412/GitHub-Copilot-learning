package com.example.taskflow.repository;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * Composable {@link Specification} factories for task queries.
 *
 * <p>Every factory returns a non-null specification: when its filter is absent
 * it yields a no-op predicate instead of {@code null}. That makes the call site
 * a plain unconditional chain, with no null checks and no
 * {@code :param IS NULL OR ...} JPQL gymnastics:</p>
 *
 * <pre>{@code
 * Specification<Task> spec = TaskSpecifications.hasStatus(status)
 *         .and(TaskSpecifications.hasPriority(priority))
 *         .and(TaskSpecifications.matchesText(query));
 * }</pre>
 */
public final class TaskSpecifications {

    private TaskSpecifications() {
        // static factory holder
    }

    /**
     * Matches everything — the identity element for {@code and} chaining.
     *
     * @return a specification that imposes no restriction
     */
    public static Specification<Task> unrestricted() {
        return (root, query, cb) -> cb.conjunction();
    }

    /**
     * @param status state to match, or {@code null} for no restriction
     * @return specification restricting to that state
     */
    public static Specification<Task> hasStatus(TaskStatus status) {
        if (status == null) {
            return unrestricted();
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    /**
     * @param priority priority to match, or {@code null} for no restriction
     * @return specification restricting to that priority
     */
    public static Specification<Task> hasPriority(TaskPriority priority) {
        if (priority == null) {
            return unrestricted();
        }
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }

    /**
     * @param assignee assignee to match case-insensitively, or blank for no restriction
     * @return specification restricting to that assignee
     */
    public static Specification<Task> hasAssignee(String assignee) {
        if (!StringUtils.hasText(assignee)) {
            return unrestricted();
        }
        String normalized = assignee.trim().toLowerCase();
        return (root, query, cb) -> cb.equal(cb.lower(root.get("assignee")), normalized);
    }

    /**
     * Free-text search across title and description.
     *
     * @param text search term, or blank for no restriction
     * @return specification matching either field, case-insensitively
     */
    public static Specification<Task> matchesText(String text) {
        if (!StringUtils.hasText(text)) {
            return unrestricted();
        }
        String pattern = "%" + text.trim().toLowerCase() + "%";
        return (root, query, cb) -> {
            Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
            Predicate descriptionMatch = cb.like(
                    cb.lower(cb.coalesce(root.get("description"), "")), pattern);
            return cb.or(titleMatch, descriptionMatch);
        };
    }

    /**
     * @param overdueOnly when {@code true}, restrict to tasks past due and not done
     * @return specification for overdue tasks, or no restriction when {@code false}
     */
    public static Specification<Task> isOverdue(boolean overdueOnly) {
        if (!overdueOnly) {
            return unrestricted();
        }
        return (root, query, cb) -> cb.and(
                cb.isNotNull(root.get("dueDate")),
                cb.lessThan(root.get("dueDate"), cb.literal(LocalDate.now())),
                cb.notEqual(root.get("status"), TaskStatus.DONE));
    }
}
