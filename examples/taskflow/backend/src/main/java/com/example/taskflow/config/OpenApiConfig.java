package com.example.taskflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI document metadata.
 *
 * <p>Browse the generated docs at {@code http://localhost:8080/swagger-ui.html}
 * once the app is running. A published schema is also the fastest way to give
 * Copilot an accurate picture of the API when generating a frontend client.</p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskflowOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskFlow API")
                        .version("1.0.0")
                        .description("""
                                Reference API for the GitHub Copilot mastery project.

                                Demonstrates a fully-layered Spring Boot service: validation,
                                dynamic filtering with JPA Specifications, optimistic locking,
                                lifecycle rules enforced in the domain, and RFC 9457
                                problem+json error responses.""")
                        .contact(new Contact().name("TaskFlow Reference Project"))
                        .license(new License().name("MIT")));
    }
}
