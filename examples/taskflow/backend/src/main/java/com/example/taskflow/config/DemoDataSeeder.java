package com.example.taskflow.config;

import com.example.taskflow.domain.Task;
import com.example.taskflow.domain.TaskPriority;
import com.example.taskflow.domain.TaskStatus;
import com.example.taskflow.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

/**
 * Seeds demo tasks so the UI has something to render on first run.
 *
 * <p>Gated behind {@code taskflow.seed-demo-data}, which is {@code true} in
 * {@code application.yml} and {@code false} in the test profile — tests must
 * control their own fixtures, never inherit demo rows.</p>
 */
@Configuration
@ConditionalOnProperty(name = "taskflow.seed-demo-data", havingValue = "true")
public class DemoDataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    @Bean
    public ApplicationRunner seedDemoTasks(TaskRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                log.info("Demo data already present ({} tasks); skipping seed", repository.count());
                return;
            }

            LocalDate today = LocalDate.now();

            Task overdueReview = new Task(
                    "Review Q3 architecture proposal",
                    "Gather feedback from the platform team before the design review.",
                    TaskPriority.HIGH, "priya", today.minusDays(3));

            Task migrateAuth = new Task(
                    "Migrate auth service to OIDC",
                    "Replace the legacy session cookie flow. Needs a staged rollout plan.",
                    TaskPriority.URGENT, "sam", today.plusDays(10));
            migrateAuth.transitionTo(TaskStatus.IN_PROGRESS);

            Task writeRunbook = new Task(
                    "Write on-call runbook for TaskFlow",
                    "Cover restart, rollback, and the top three alerts.",
                    TaskPriority.MEDIUM, "jordan", today.plusDays(4));

            Task upgradeDeps = new Task(
                    "Upgrade frontend dependencies",
                    "Bump React and Vite, then re-run the visual regression suite.",
                    TaskPriority.LOW, "sam", null);
            upgradeDeps.transitionTo(TaskStatus.IN_PROGRESS);
            upgradeDeps.transitionTo(TaskStatus.DONE);

            Task instrumentApi = new Task(
                    "Add request tracing to the task API",
                    "Propagate trace ids from the frontend through to the database span.",
                    TaskPriority.MEDIUM, null, today.plusDays(21));

            repository.saveAll(List.of(
                    overdueReview, migrateAuth, writeRunbook, upgradeDeps, instrumentApi));

            log.info("Seeded {} demo tasks", repository.count());
        };
    }
}
