package com.fileprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for file upload response.
 * Uses Builder pattern for flexible object construction.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {

    private String message;
    private String originalFileName;
    private String outputFileName;
    private String downloadUrl;
    private Long fileSize;
    private String processingTime;
}
