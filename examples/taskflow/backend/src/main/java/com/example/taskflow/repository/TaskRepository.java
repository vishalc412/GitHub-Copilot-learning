package com.example.taskflow.repository;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Data access for {@link Task}.
 *
 * <p>Extends {@link JpaSpecificationExecutor} so dynamic, multi-field filtering
 * is composed type-safely in {@link TaskSpecifications} rather than assembled
 * as JPQL strings with {@code :param IS NULL OR ...} clauses.</p>
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    /**
     * Counts tasks currently in the given state. Used by the stats endpoint.
     *
     * @param status state to count
     * @return number of matching tasks
     */
    long countByStatus(TaskStatus status);

    /**
     * @param title title to test, compared case-insensitively
     * @return {@code true} when a task with this title already exists
     */
    boolean existsByTitleIgnoreCase(String title);
}
