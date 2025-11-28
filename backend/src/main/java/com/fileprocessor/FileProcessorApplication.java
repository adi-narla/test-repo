package com.fileprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the File Processor service.
 * Provides REST API endpoints for file upload and image processing.
 */
@SpringBootApplication
public class FileProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileProcessorApplication.class, args);
    }
}
