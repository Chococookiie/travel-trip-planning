package com.travelplanner.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI Configuration for API Documentation.
 * Automatically generates interactive API documentation at:
 * - http://localhost:8080/swagger-ui.html
 * - http://localhost:8080/v3/api-docs (JSON format)
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure OpenAPI documentation.
     *
     * @return OpenAPI configuration bean
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")))
                .info(new Info()
                        .title("Travel Trip Planning API")
                        .version("1.0.0")
                        .description("""
                                Comprehensive REST API for Travel Trip Planning Application.
                                
                                Features:
                                - User authentication with JWT tokens
                                - Flight search and booking
                                - Train search and booking
                                - Search history management
                                - Price alerts and notifications
                                
                                All endpoints except /api/auth/register and /api/auth/login require authentication.
                                Use the JWT token obtained from login/register in the Authorization header: Bearer <token>
                                """)
                        .termsOfService("https://example.com/terms")
                        .contact(new Contact()
                                .name("Travel Planner Support")
                                .email("support@travelplanner.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
