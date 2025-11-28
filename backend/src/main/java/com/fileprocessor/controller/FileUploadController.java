package com.fileprocessor.controller;

import com.fileprocessor.dto.FileUploadResponse;
import com.fileprocessor.exception.InvalidFileException;
import com.fileprocessor.service.IFileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for handling file upload and download operations.
 * Uses constructor injection for better testability and immutability.
 */
@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FileUploadController {

    private final IFileProcessingService fileProcessingService;

    /**
     * Handles file upload and processing.
     * Exceptions are handled by GlobalExceptionHandler.
     *
     * @param file the multipart file to be uploaded and processed
     * @return ResponseEntity containing upload details
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new com.fileprocessor.exception.InvalidFileException("Please select a file to upload");
        }

        long startTime = System.currentTimeMillis();
        String outputFileName = fileProcessingService.processFile(file);
        long processingTime = System.currentTimeMillis() - startTime;

        FileUploadResponse response = FileUploadResponse.builder()
                .message("File processed successfully")
                .originalFileName(file.getOriginalFilename())
                .outputFileName(outputFileName)
                .downloadUrl("/api/files/download/" + outputFileName)
                .fileSize(file.getSize())
                .processingTime(processingTime + "ms")
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Downloads a processed file by filename.
     *
     * @param filename the name of the file to download
     * @return ResponseEntity containing the file resource or error status
     */
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = fileProcessingService.getOutputFile(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Health check endpoint to verify service availability.
     *
     * @return ResponseEntity with service status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }
}
