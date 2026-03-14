package com.cloudanalytics.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String port;

    @Bean
    public OpenAPI cloudAnalyticsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cloud Analytics Pipeline API")
                        .description("""
                            Real-time cloud analytics pipeline — built with Spring Boot 3, PostgreSQL, Redis, and Docker.
                            Designed for SAP-grade reliability and Agile CI/CD workflows.
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Cloud Analytics Team")
                                .email("team@cloudanalytics.io"))
                        .license(new License().name("MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:" + port).description("Local Development"),
                        new Server().url("https://api.cloudanalytics.io").description("Production")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token")));
    }
}
