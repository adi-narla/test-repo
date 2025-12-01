package com.fileprocessor.exception;

import com.fileprocessor.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GlobalExceptionHandler.
 * Tests exception handling and error response generation.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/files/upload");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    void handleFileProcessingException_ShouldReturnInternalServerError() {
        // Arrange
        FileProcessingException exception = new FileProcessingException("Processing failed");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleFileProcessingException(exception, webRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("File Processing Error");
        assertThat(response.getBody().getMessage()).isEqualTo("Processing failed");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleFileStorageException_ShouldReturnInternalServerError() {
        // Arrange
        FileStorageException exception = new FileStorageException("Storage failed");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleFileStorageException(exception, webRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("File Storage Error");
        assertThat(response.getBody().getMessage()).isEqualTo("Storage failed");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }

    @Test
    void handleInvalidFileException_ShouldReturnBadRequest() {
        // Arrange
        InvalidFileException exception = new InvalidFileException("Invalid file type");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidFileException(exception, webRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Invalid File");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid file type");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }

    @Test
    void handleMaxSizeException_ShouldReturnPayloadTooLarge() {
        // Arrange
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(1024);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMaxSizeException(exception, webRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("File Too Large");
        assertThat(response.getBody().getMessage()).isEqualTo("File size exceeds maximum allowed limit");
        assertThat(response.getBody().getStatus()).isEqualTo(413);
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerError() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }

    @Test
    void allExceptionHandlers_ShouldIncludeTimestampAndPath() {
        // Arrange
        InvalidFileException exception = new InvalidFileException("Test error");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidFileException(exception, webRequest);

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getPath()).contains("/api/files/upload");
    }
}
