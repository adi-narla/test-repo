package com.fileprocessor.controller;

import com.fileprocessor.dto.ErrorResponse;
import com.fileprocessor.dto.FileUploadResponse;
import com.fileprocessor.exception.InvalidFileException;
import com.fileprocessor.processor.ProcessorType;
import com.fileprocessor.service.IFileProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "File Processing", description = "APIs for uploading and processing image files")
public class FileUploadController {

    private final IFileProcessingService fileProcessingService;

    /**
     * Handles file upload and processing.
     * Exceptions are handled by GlobalExceptionHandler.
     *
     * @param file the multipart file to be uploaded and processed
     * @param processorType optional processor type (defaults to GRAYSCALE_BORDER)
     * @return ResponseEntity containing upload details
     */
    @Operation(
            summary = "Upload and process an image file",
            description = "Upload an image file and apply the selected processing effect. Supports PNG, JPEG, GIF, and WebP formats up to 10MB.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File processed successfully",
                    content = @Content(schema = @Schema(implementation = FileUploadResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file or bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "413", description = "File too large",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Image processing type: GRAYSCALE_BORDER (default), SEPIA, or BLUR",
                    example = "GRAYSCALE_BORDER")
            @RequestParam(value = "processorType", required = false, defaultValue = "GRAYSCALE_BORDER")
            ProcessorType processorType) {

        if (file.isEmpty()) {
            throw new InvalidFileException("Please select a file to upload");
        }

        long startTime = System.currentTimeMillis();
        String outputFileName = fileProcessingService.processFile(file, processorType);
        long processingTime = System.currentTimeMillis() - startTime;

        FileUploadResponse response = FileUploadResponse.builder()
                .message("File processed successfully with " + processorType + " processor")
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
    @Operation(
            summary = "Download a processed image file",
            description = "Download a previously processed image file by its filename.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully",
                    content = @Content(mediaType = "image/png")),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Name of the processed file", required = true, example = "processed_abc123.png")
            @PathVariable String filename) {
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
    @Operation(
            summary = "Health check endpoint",
            description = "Check if the file processing service is running and healthy. Public endpoint, no authentication required."
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }
}
