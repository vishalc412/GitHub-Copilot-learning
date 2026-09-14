package com.example.taskflow.web;

import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import com.example.taskflow.service.TaskService;
import com.example.taskflow.web.dto.CreateTaskRequest;
import com.example.taskflow.web.dto.PageResponse;
import com.example.taskflow.web.dto.TaskResponse;
import com.example.taskflow.web.dto.TaskStatsResponse;
import com.example.taskflow.web.dto.UpdateStatusRequest;
import com.example.taskflow.web.dto.UpdateTaskRequest;
import com.example.taskflow.web.mapper.TaskMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * HTTP API for tasks.
 *
 * <p>A thin adapter: it binds and validates input, delegates to
 * {@link TaskService}, and maps the result to DTOs. No business rules live
 * here — that separation is what makes the service unit-testable without a
 * servlet container.</p>
 */
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Create, query, and progress work items")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    /**
     * Lists tasks with optional filtering, sorting, and pagination.
     *
     * @param status      filter by lifecycle state
     * @param priority    filter by urgency
     * @param assignee    filter by assignee, case-insensitive
     * @param q           free-text search over title and description
     * @param overdueOnly when true, only tasks past due and not done
     * @param pageable    page/size/sort, defaulting to newest first
     * @return a page of tasks
     */
    @GetMapping
    @Operation(summary = "List tasks",
            description = "Supports filtering, free-text search, sorting, and pagination.")
    public PageResponse<TaskResponse> list(
            @Parameter(description = "Filter by status") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Filter by priority") @RequestParam(required = false) TaskPriority priority,
            @Parameter(description = "Filter by assignee") @RequestParam(required = false) String assignee,
            @Parameter(description = "Search title and description") @RequestParam(required = false) String q,
            @Parameter(description = "Only overdue tasks") @RequestParam(defaultValue = "false") boolean overdueOnly,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return PageResponse.from(
                service.search(status, priority, assignee, q, overdueOnly, pageable),
                TaskMapper::toResponse);
    }

    /**
     * @return aggregate counts by status with a derived completion rate
     */
    @GetMapping("/stats")
    @Operation(summary = "Task statistics")
    public TaskStatsResponse stats() {
        return service.stats();
    }

    /**
     * @param id task identifier
     * @return the task
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "No task with that id")
    })
    public TaskResponse getById(@PathVariable Long id) {
        return TaskMapper.toResponse(service.findById(id));
    }

    /**
     * Creates a task.
     *
     * @param request validated payload
     * @param uriBuilder injected builder used for the {@code Location} header
     * @return 201 Created with the new task and its location
     */
    @PostMapping
    @Operation(summary = "Create a task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Title already in use")
    })
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request,
                                              UriComponentsBuilder uriBuilder) {
        TaskResponse created = TaskMapper.toResponse(service.create(request));
        URI location = uriBuilder.path("/api/v1/tasks/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Replaces a task's editable fields.
     *
     * @param id      task identifier
     * @param request validated payload
     * @return the updated task
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a task",
            description = "Replaces editable fields. Status changes use PATCH /{id}/status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "404", description = "No task with that id"),
            @ApiResponse(responseCode = "409", description = "Title already in use")
    })
    public TaskResponse update(@PathVariable Long id,
                               @Valid @RequestBody UpdateTaskRequest request) {
        return TaskMapper.toResponse(service.update(id, request));
    }

    /**
     * Moves a task through its lifecycle.
     *
     * @param id      task identifier
     * @param request the target status
     * @return the updated task
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Change task status",
            description = "Enforces the lifecycle: TODO ⇄ IN_PROGRESS → DONE (DONE is terminal).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status changed"),
            @ApiResponse(responseCode = "404", description = "No task with that id"),
            @ApiResponse(responseCode = "409", description = "Illegal transition")
    })
    public TaskResponse changeStatus(@PathVariable Long id,
                                     @Valid @RequestBody UpdateStatusRequest request) {
        return TaskMapper.toResponse(service.changeStatus(id, request.status()));
    }

    /**
     * Deletes a task.
     *
     * @param id task identifier
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "No task with that id")
    })
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
