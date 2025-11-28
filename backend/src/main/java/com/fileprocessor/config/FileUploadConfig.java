package com.fileprocessor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Configuration properties for file upload settings.
 * Uses @ConfigurationProperties for type-safe configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "file.upload")
@Validated
public class FileUploadConfig {

    @NotBlank(message = "Upload directory must be specified")
    private String uploadDir = "uploads";

    @NotBlank(message = "Output directory must be specified")
    private String outputDir = "uploads/output";

    @Positive(message = "Max file size must be positive")
    private long maxFileSize = 10485760; // 10MB in bytes

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
