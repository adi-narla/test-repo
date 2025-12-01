package com.fileprocessor.service;

import com.fileprocessor.exception.FileProcessingException;
import com.fileprocessor.exception.InvalidFileException;
import com.fileprocessor.processor.ImageProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FileProcessingService.
 * Uses Mockito for mocking dependencies and JUnit 5 for test execution.
 */
@ExtendWith(MockitoExtension.class)
class FileProcessingServiceTest {

    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private MultipartFile multipartFile;

    private FileProcessingService fileProcessingService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileProcessingService = new FileProcessingService(imageProcessor);
        ReflectionTestUtils.setField(fileProcessingService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(fileProcessingService, "outputDir", tempDir.resolve("output").toString());
    }

    @Test
    void processFile_WithValidImage_ShouldReturnOutputFileName() throws IOException {
        // Arrange
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        byte[] imageBytes = createImageBytes(testImage);

        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getOriginalFilename()).thenReturn("test.png");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(imageBytes));
        when(imageProcessor.process(any(BufferedImage.class))).thenReturn(testImage);

        // Act
        String result = fileProcessingService.processFile(multipartFile);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).startsWith("processed_");
        assertThat(result).endsWith(".png");
        verify(imageProcessor, times(1)).process(any(BufferedImage.class));
    }

    @Test
    void processFile_WithNullContentType_ShouldThrowInvalidFileException() {
        // Arrange
        when(multipartFile.getContentType()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> fileProcessingService.processFile(multipartFile))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("File must be an image");
    }

    @Test
    void processFile_WithNonImageContentType_ShouldThrowInvalidFileException() {
        // Arrange
        when(multipartFile.getContentType()).thenReturn("application/pdf");

        // Act & Assert
        assertThatThrownBy(() -> fileProcessingService.processFile(multipartFile))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("File must be an image");
    }

    @Test
    void processFile_WithInvalidImageData_ShouldThrowFileProcessingException() throws IOException {
        // Arrange
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getOriginalFilename()).thenReturn("test.png");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("invalid data".getBytes()));

        // Act & Assert
        assertThatThrownBy(() -> fileProcessingService.processFile(multipartFile))
                .isInstanceOf(FileProcessingException.class);
    }

    @Test
    void processFile_WhenImageProcessorFails_ShouldThrowFileProcessingException() throws IOException {
        // Arrange
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        byte[] imageBytes = createImageBytes(testImage);

        when(multipartFile.getContentType()).thenReturn("image/png");
        when(multipartFile.getOriginalFilename()).thenReturn("test.png");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(imageBytes));
        when(imageProcessor.process(any(BufferedImage.class))).thenThrow(new RuntimeException("Processing failed"));

        // Act & Assert
        assertThatThrownBy(() -> fileProcessingService.processFile(multipartFile))
                .isInstanceOf(FileProcessingException.class);
    }

    @Test
    void getOutputFile_ShouldReturnCorrectPath() throws IOException {
        // Arrange
        Files.createDirectories(tempDir.resolve("output"));
        String filename = "test_output.png";

        // Act
        Path result = fileProcessingService.getOutputFile(filename);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFileName().toString()).isEqualTo(filename);
    }

    private byte[] createImageBytes(BufferedImage image) throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
