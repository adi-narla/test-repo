package com.fileprocessor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration for API documentation.
 * Accessible at /swagger-ui.html and /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fileProcessorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("File Processor API")
                        .description("REST API for uploading and processing image files with various effects")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("File Processor Team")
                                .email("support@fileprocessor.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Basic Authentication with username and password")));
    }
}
