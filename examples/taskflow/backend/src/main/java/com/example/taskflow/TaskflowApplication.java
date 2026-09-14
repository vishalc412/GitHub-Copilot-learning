package com.example.taskflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TaskFlow API entry point.
 *
 * <p>A deliberately small but fully-layered Spring Boot service used as the
 * backend half of the GitHub Copilot reference project. Every layer a real
 * service has is present — entity, repository, service, web, DTO mapping,
 * validation, error handling — so Copilot has realistic patterns to learn
 * from and extend.</p>
 *
 * <p>Run locally with: {@code mvn spring-boot:run} (port 8080).</p>
 *
 * <p>Note what is <em>not</em> here: {@code @EnableJpaAuditing} lives in
 * {@link com.example.taskflow.config.JpaAuditingConfig} instead. Putting it on
 * this class breaks every {@code @WebMvcTest} in the project — that class
 * explains exactly why.</p>
 */
@SpringBootApplication
public class TaskflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskflowApplication.class, args);
    }
}
