package com.example.taskflow.web;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import com.example.taskflow.service.TaskService;
import com.example.taskflow.service.exception.DuplicateTaskTitleException;
import com.example.taskflow.service.exception.IllegalStatusTransitionException;
import com.example.taskflow.service.exception.TaskNotFoundException;
import com.example.taskflow.web.dto.CreateTaskRequest;
import com.example.taskflow.web.dto.TaskStatsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests: HTTP contract only, service mocked.
 *
 * <p>{@code @WebMvcTest} loads the controller, the JSON converters, and
 * {@link GlobalExceptionHandler} — nothing else. That makes this the right
 * place to assert status codes, headers, and the problem+json error shape,
 * and the wrong place to assert business rules.</p>
 *
 * <p>Two Spring Boot 4 specifics worth noting:</p>
 * <ul>
 *   <li>{@code @MockitoBean} replaces the removed {@code @MockBean}.</li>
 *   <li>Request bodies are JSON string literals rather than objects run through
 *       an autowired {@code ObjectMapper}. Boot 4 ships <strong>Jackson 3</strong>
 *       (package {@code tools.jackson}), so autowiring
 *       {@code com.fasterxml.jackson.databind.ObjectMapper} fails with
 *       "No qualifying bean". Literals also make the test assert the real wire
 *       format instead of whatever a serializer happens to produce.</li>
 * </ul>
 */
@WebMvcTest(TaskController.class)
@DisplayName("TaskController HTTP contract")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService service;

    /** Builds a persisted-looking task without needing a database. */
    private static Task taskFixture(String title, TaskStatus status) {
        Task task = new Task(title, "description", TaskPriority.HIGH,
                "priya", LocalDate.now().plusDays(2));
        if (status == TaskStatus.IN_PROGRESS || status == TaskStatus.DONE) {
            task.transitionTo(TaskStatus.IN_PROGRESS);
        }
        if (status == TaskStatus.DONE) {
            task.transitionTo(TaskStatus.DONE);
        }
        return task;
    }

    @Nested
    @DisplayName("GET /api/v1/tasks")
    class ListTasks {

        @Test
        @DisplayName("returns a stable page envelope")
        void returnsPageEnvelope() throws Exception {
            Page<Task> page = new PageImpl<>(
                    List.of(taskFixture("First task", TaskStatus.TODO)),
                    PageRequest.of(0, 20), 1);
            when(service.search(isNull(), isNull(), isNull(), isNull(), anyBoolean(), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/api/v1/tasks"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", org.hamcrest.Matchers.hasSize(1)))
                    .andExpect(jsonPath("$.content[0].title").value("First task"))
                    .andExpect(jsonPath("$.content[0].status").value("TODO"))
                    .andExpect(jsonPath("$.content[0].allowedTransitions[0]").value("IN_PROGRESS"))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.last").value(true));
        }

        @Test
        @DisplayName("passes filters through to the service")
        void passesFiltersThrough() throws Exception {
            when(service.search(any(), any(), any(), any(), anyBoolean(), any(Pageable.class)))
                    .thenReturn(Page.empty());

            mockMvc.perform(get("/api/v1/tasks")
                            .param("status", "IN_PROGRESS")
                            .param("priority", "URGENT")
                            .param("assignee", "sam")
                            .param("q", "billing")
                            .param("overdueOnly", "true"))
                    .andExpect(status().isOk());

            verify(service).search(eq(TaskStatus.IN_PROGRESS), eq(TaskPriority.URGENT),
                    eq("sam"), eq("billing"), eq(true), any(Pageable.class));
        }

        @Test
        @DisplayName("rejects an unparseable enum with 400 problem+json")
        void rejectsBadEnum() throws Exception {
            mockMvc.perform(get("/api/v1/tasks").param("status", "NOT_A_STATUS"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.title").value("Invalid parameter"))
                    .andExpect(jsonPath("$.parameter").value("status"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/tasks/{id}")
    class GetOne {

        @Test
        @DisplayName("returns the task")
        void returnsTask() throws Exception {
            when(service.findById(1L)).thenReturn(taskFixture("Found", TaskStatus.IN_PROGRESS));

            mockMvc.perform(get("/api/v1/tasks/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Found"))
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        }

        @Test
        @DisplayName("maps TaskNotFoundException to 404 problem+json")
        void mapsNotFound() throws Exception {
            when(service.findById(404L)).thenThrow(new TaskNotFoundException(404L));

            mockMvc.perform(get("/api/v1/tasks/404"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.title").value("Task not found"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.type").value("https://taskflow.example/problems/task-not-found"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/tasks")
    class CreateTask {

        @Test
        @DisplayName("returns 201 with a Location header")
        void returnsCreated() throws Exception {
            when(service.create(any(CreateTaskRequest.class)))
                    .thenReturn(taskFixture("Brand new", TaskStatus.TODO));

            String body = """
                    {
                      "title": "Brand new",
                      "description": "with detail",
                      "priority": "HIGH",
                      "assignee": "priya",
                      "dueDate": "2026-12-31"
                    }""";

            mockMvc.perform(post("/api/v1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.title").value("Brand new"));
        }

        @Test
        @DisplayName("reports every invalid field, not just the first")
        void reportsAllValidationErrors() throws Exception {
            String body = """
                    {
                      "title": "ab",
                      "assignee": "%s"
                    }
                    """.formatted("x".repeat(200));

            mockMvc.perform(post("/api/v1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.title").value("Validation failed"))
                    .andExpect(jsonPath("$.errors.title").exists())
                    .andExpect(jsonPath("$.errors.assignee").exists());
        }

        @Test
        @DisplayName("maps a duplicate title to 409")
        void mapsDuplicateTitle() throws Exception {
            when(service.create(any(CreateTaskRequest.class)))
                    .thenThrow(new DuplicateTaskTitleException("Taken"));

            mockMvc.perform(post("/api/v1/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": "Taken", "priority": "LOW"}"""))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.conflictingTitle").value("Taken"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/tasks/{id}/status")
    class ChangeStatus {

        @Test
        @DisplayName("applies a legal transition")
        void appliesTransition() throws Exception {
            when(service.changeStatus(1L, TaskStatus.IN_PROGRESS))
                    .thenReturn(taskFixture("Moving", TaskStatus.IN_PROGRESS));

            mockMvc.perform(patch("/api/v1/tasks/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"status": "IN_PROGRESS"}"""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        }

        @Test
        @DisplayName("maps an illegal transition to 409 with from/to detail")
        void mapsIllegalTransition() throws Exception {
            when(service.changeStatus(anyLong(), eq(TaskStatus.DONE)))
                    .thenThrow(new IllegalStatusTransitionException(TaskStatus.TODO, TaskStatus.DONE));

            mockMvc.perform(patch("/api/v1/tasks/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"status": "DONE"}"""))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.title").value("Illegal status transition"))
                    .andExpect(jsonPath("$.from").value("TODO"))
                    .andExpect(jsonPath("$.to").value("DONE"));
        }

        @Test
        @DisplayName("rejects a missing status field")
        void rejectsMissingStatus() throws Exception {
            mockMvc.perform(patch("/api/v1/tasks/1/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.status").exists());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/tasks/{id}")
    class DeleteTask {

        @Test
        @DisplayName("returns 204 on success")
        void returnsNoContent() throws Exception {
            doNothing().when(service).delete(1L);

            mockMvc.perform(delete("/api/v1/tasks/1"))
                    .andExpect(status().isNoContent());

            verify(service).delete(1L);
        }

        @Test
        @DisplayName("returns 404 when the task is gone")
        void returnsNotFound() throws Exception {
            doThrow(new TaskNotFoundException(404L)).when(service).delete(404L);

            mockMvc.perform(delete("/api/v1/tasks/404"))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("GET /api/v1/tasks/stats returns aggregate counts")
    void returnsStats() throws Exception {
        when(service.stats()).thenReturn(TaskStatsResponse.of(2, 3, 5));

        mockMvc.perform(get("/api/v1/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.done").value(5))
                .andExpect(jsonPath("$.completionRate").value(0.5));
    }
}
