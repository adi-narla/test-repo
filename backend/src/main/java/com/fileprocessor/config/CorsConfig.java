package com.fileprocessor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS configuration for allowing cross-origin requests from the Angular frontend.
 * Configures allowed origins, methods, and headers for API endpoints.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configure CORS mappings for all API endpoints.
     * Allows requests from Angular development server on localhost:4200.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
