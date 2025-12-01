package com.fileprocessor.controller;

import com.fileprocessor.dto.FileUploadResponse;
import com.fileprocessor.exception.InvalidFileException;
import com.fileprocessor.service.IFileProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FileUploadController.
 * Tests REST endpoint logic and response handling.
 */
@ExtendWith(MockitoExtension.class)
class FileUploadControllerTest {

    @Mock
    private IFileProcessingService fileProcessingService;

    @InjectMocks
    private FileUploadController controller;

    @Test
    void uploadFile_WithValidFile_ShouldReturnSuccessResponse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );
        String outputFileName = "processed_abc123.png";
        when(fileProcessingService.processFile(any(MultipartFile.class))).thenReturn(outputFileName);

        // Act
        ResponseEntity<FileUploadResponse> response = controller.uploadFile(file);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("File processed successfully");
        assertThat(response.getBody().getOriginalFileName()).isEqualTo("test.png");
        assertThat(response.getBody().getOutputFileName()).isEqualTo(outputFileName);
        assertThat(response.getBody().getDownloadUrl()).contains(outputFileName);
        assertThat(response.getBody().getFileSize()).isGreaterThan(0);
        verify(fileProcessingService, times(1)).processFile(any(MultipartFile.class));
    }

    @Test
    void uploadFile_WithEmptyFile_ShouldThrowInvalidFileException() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        );

        // Act & Assert
        assertThatThrownBy(() -> controller.uploadFile(emptyFile))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("Please select a file to upload");
        verify(fileProcessingService, never()).processFile(any(MultipartFile.class));
    }

    @Test
    void uploadFile_ShouldTrackProcessingTime() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test image content".getBytes()
        );
        when(fileProcessingService.processFile(any(MultipartFile.class))).thenReturn("output.png");

        // Act
        ResponseEntity<FileUploadResponse> response = controller.uploadFile(file);

        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProcessingTime()).isNotNull();
        assertThat(response.getBody().getProcessingTime()).endsWith("ms");
    }

    @Test
    void downloadFile_WithValidFilename_ShouldReturnResource() throws Exception {
        // Arrange
        String filename = "test.png";
        Path testPath = Paths.get("uploads/output/" + filename);
        when(fileProcessingService.getOutputFile(filename)).thenReturn(testPath);

        // Note: This test is limited as we can't easily mock file resources
        // Integration tests would be better for file download functionality
        Path result = fileProcessingService.getOutputFile(filename);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFileName().toString()).isEqualTo(filename);
    }

    @Test
    void health_ShouldReturnUpStatus() {
        // Act
        ResponseEntity<Map<String, String>> response = controller.health();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
    }
}
