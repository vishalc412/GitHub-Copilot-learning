package com.example.taskflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables Spring Data JPA auditing, which populates {@code @CreatedDate} and
 * {@code @LastModifiedDate} on entities.
 *
 * <p><strong>Why this lives in its own class rather than on
 * {@code TaskflowApplication}:</strong> sliced tests such as
 * {@code @WebMvcTest} load the {@code @SpringBootApplication} class for its
 * configuration but deliberately exclude JPA auto-configuration. If
 * {@code @EnableJpaAuditing} sits on the application class, the web slice tries
 * to build an auditing handler with no entities present and the whole context
 * fails with:</p>
 *
 * <pre>
 * BeanCreationException: Error creating bean with name 'jpaAuditingHandler'
 *   Caused by: IllegalArgumentException: JPA metamodel must not be empty
 * </pre>
 *
 * <p>Keeping it in a standalone {@code @Configuration} means the web slice
 * never sees it (slice tests do not component-scan arbitrary configuration),
 * while {@code @SpringBootTest} picks it up normally. Tests that need auditing
 * but run a narrower slice — see {@code TaskRepositoryTest} — import this class
 * explicitly with {@code @Import(JpaAuditingConfig.class)}.</p>
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
