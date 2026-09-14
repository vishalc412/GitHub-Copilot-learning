package com.example.taskflow;

import com.example.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-stack tests: real controller, real service, real repository, real H2.
 *
 * <p>Nothing is mocked, so these prove the layers are wired together — that
 * validation actually fires, that the lifecycle rule actually blocks a bad
 * transition through the HTTP layer, and that a create is really persisted and
 * readable back. Slower than the sliced tests, so there are deliberately few
 * of them and each covers a whole journey rather than one branch.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Task API end to end")
class TaskApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void startFromCleanState() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("full lifecycle: create -> read -> update -> progress -> complete -> delete")
    void fullLifecycle() throws Exception {
        // 1. Create
        MvcResult created = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Integrate payment provider",
                                  "description": "Sandbox first, then production keys",
                                  "priority": "HIGH",
                                  "assignee": "priya",
                                  "dueDate": "2026-12-01"
                                }"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.overdue").value(false))
                .andReturn();

        Integer id = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.id");

        // 2. Read it back
        mockMvc.perform(get("/api/v1/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integrate payment provider"))
                .andExpect(jsonPath("$.assignee").value("priya"));

        // 3. Update editable fields
        mockMvc.perform(put("/api/v1/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Integrate payment provider v2",
                                  "description": "Scope cut to sandbox only",
                                  "priority": "URGENT",
                                  "assignee": "sam",
                                  "dueDate": "2026-11-15"
                                }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priority").value("URGENT"))
                .andExpect(jsonPath("$.assignee").value("sam"))
                // PUT must not reset the lifecycle state
                .andExpect(jsonPath("$.status").value("TODO"));

        // 4. TODO -> DONE is illegal and must be refused
        mockMvc.perform(patch("/api/v1/tasks/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "DONE"}"""))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.from").value("TODO"))
                .andExpect(jsonPath("$.to").value("DONE"));

        // 5. TODO -> IN_PROGRESS -> DONE is the legal path
        mockMvc.perform(patch("/api/v1/tasks/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "IN_PROGRESS"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(patch("/api/v1/tasks/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "DONE"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.allowedTransitions").isEmpty());

        // 6. Stats reflect the completed task
        mockMvc.perform(get("/api/v1/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.done").value(1))
                .andExpect(jsonPath("$.completionRate").value(1.0));

        // 7. Delete, then confirm it is really gone
        mockMvc.perform(delete("/api/v1/tasks/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/tasks/{id}", id))
                .andExpect(status().isNotFound());

        assertThat(repository.count()).isZero();
    }

    @Test
    @DisplayName("duplicate titles are rejected across requests")
    void rejectsDuplicateTitleAcrossRequests() throws Exception {
        String body = """
                {"title": "Only one of these", "priority": "LOW"}""";

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        // Same title, different casing — still a conflict.
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "ONLY ONE OF THESE", "priority": "LOW"}"""))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Duplicate task title"));

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("validation rejects a too-short title before reaching the database")
    void rejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "no"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());

        assertThat(repository.count()).isZero();
    }

    @Test
    @DisplayName("an unknown path is a 404, not a 500")
    void unknownPathIsNotFound() throws Exception {
        // Regression guard: the catch-all @ExceptionHandler(Exception.class) in
        // GlobalExceptionHandler used to swallow Spring's NoResourceFoundException
        // and report 500 for every mistyped URL.
        mockMvc.perform(get("/api/v1/definitely-not-a-real-endpoint"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    @DisplayName("filtering and search work against real SQL")
    void filtersAndSearches() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Database migration plan", "description": "Postgres cutover",
                         "priority": "URGENT", "assignee": "priya"}"""))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Frontend polish", "description": "Spacing and focus states",
                         "priority": "LOW", "assignee": "sam"}"""))
                .andExpect(status().isCreated());

        // Filter by priority
        mockMvc.perform(get("/api/v1/tasks").param("priority", "URGENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Database migration plan"));

        // Free-text search hits the description
        mockMvc.perform(get("/api/v1/tasks").param("q", "cutover"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        // Assignee filter is case-insensitive
        mockMvc.perform(get("/api/v1/tasks").param("assignee", "SAM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Frontend polish"));

        // Pagination envelope is stable
        mockMvc.perform(get("/api/v1/tasks").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.last").value(false));
    }
}
