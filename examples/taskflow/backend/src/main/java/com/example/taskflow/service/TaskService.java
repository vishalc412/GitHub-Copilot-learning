package com.example.taskflow.service;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import com.example.taskflow.repository.TaskRepository;
import com.example.taskflow.repository.TaskSpecifications;
import com.example.taskflow.service.exception.DuplicateTaskTitleException;
import com.example.taskflow.service.exception.IllegalStatusTransitionException;
import com.example.taskflow.service.exception.TaskNotFoundException;
import com.example.taskflow.web.dto.CreateTaskRequest;
import com.example.taskflow.web.dto.TaskStatsResponse;
import com.example.taskflow.web.dto.UpdateTaskRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for tasks.
 *
 * <p>Read methods inherit the class-level read-only transaction; mutating
 * methods opt into a writable one. Business rules live here or on the domain
 * objects — never in the controller, which stays a thin HTTP adapter.</p>
 */
@Service
@Transactional(readOnly = true)
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    /**
     * Finds tasks matching any combination of filters.
     *
     * @param status      restrict to this state, or {@code null} for all
     * @param priority    restrict to this priority, or {@code null} for all
     * @param assignee    restrict to this assignee (case-insensitive), or blank for all
     * @param query       free-text search over title and description, or blank for all
     * @param overdueOnly when {@code true}, only tasks past due and not done
     * @param pageable    page, size, and sort
     * @return a page of matching tasks
     */
    public Page<Task> search(TaskStatus status,
                             TaskPriority priority,
                             String assignee,
                             String query,
                             boolean overdueOnly,
                             Pageable pageable) {

        Specification<Task> spec = TaskSpecifications.hasStatus(status)
                .and(TaskSpecifications.hasPriority(priority))
                .and(TaskSpecifications.hasAssignee(assignee))
                .and(TaskSpecifications.matchesText(query))
                .and(TaskSpecifications.isOverdue(overdueOnly));

        Page<Task> result = repository.findAll(spec, pageable);
        log.debug("Task search returned {} of {} matches (page {})",
                result.getNumberOfElements(), result.getTotalElements(), pageable.getPageNumber());
        return result;
    }

    /**
     * Loads a single task.
     *
     * @param id task identifier
     * @return the task
     * @throws TaskNotFoundException when no task has that id
     */
    public Task findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    /**
     * Creates a task in the {@link TaskStatus#TODO} state.
     *
     * @param request validated creation payload
     * @return the persisted task
     * @throws DuplicateTaskTitleException when the title is already in use
     */
    @Transactional
    public Task create(CreateTaskRequest request) {
        String title = request.title().trim();
        if (repository.existsByTitleIgnoreCase(title)) {
            throw new DuplicateTaskTitleException(title);
        }

        Task task = new Task(
                title,
                trimToNull(request.description()),
                request.priority(),
                trimToNull(request.assignee()),
                request.dueDate());

        Task saved = repository.save(task);
        log.info("Created task {} '{}'", saved.getId(), saved.getTitle());
        return saved;
    }

    /**
     * Replaces the mutable fields of an existing task.
     *
     * <p>Status is intentionally not updatable here — see
     * {@link #changeStatus(Long, TaskStatus)}.</p>
     *
     * @param id      task identifier
     * @param request validated update payload
     * @return the updated task
     * @throws TaskNotFoundException       when no task has that id
     * @throws DuplicateTaskTitleException when renaming onto another task's title
     */
    @Transactional
    public Task update(Long id, UpdateTaskRequest request) {
        Task task = findById(id);
        String newTitle = request.title().trim();

        boolean titleChanged = !newTitle.equalsIgnoreCase(task.getTitle());
        if (titleChanged && repository.existsByTitleIgnoreCase(newTitle)) {
            throw new DuplicateTaskTitleException(newTitle);
        }

        task.setTitle(newTitle);
        task.setDescription(trimToNull(request.description()));
        task.setPriority(request.priority());
        task.setAssignee(trimToNull(request.assignee()));
        task.setDueDate(request.dueDate());

        log.info("Updated task {}", id);
        return task;
    }

    /**
     * Moves a task through its lifecycle.
     *
     * @param id     task identifier
     * @param target desired next state
     * @return the updated task
     * @throws TaskNotFoundException             when no task has that id
     * @throws IllegalStatusTransitionException when the transition is not permitted
     */
    @Transactional
    public Task changeStatus(Long id, TaskStatus target) {
        Task task = findById(id);
        TaskStatus current = task.getStatus();
        try {
            task.transitionTo(target);
        } catch (IllegalStateException ex) {
            throw new IllegalStatusTransitionException(current, target);
        }
        log.info("Task {} transitioned {} -> {}", id, current, target);
        return task;
    }

    /**
     * Deletes a task.
     *
     * @param id task identifier
     * @throws TaskNotFoundException when no task has that id
     */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
        log.info("Deleted task {}", id);
    }

    /**
     * @return aggregate counts by status, plus a derived completion rate
     */
    public TaskStatsResponse stats() {
        return TaskStatsResponse.of(
                repository.countByStatus(TaskStatus.TODO),
                repository.countByStatus(TaskStatus.IN_PROGRESS),
                repository.countByStatus(TaskStatus.DONE));
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
