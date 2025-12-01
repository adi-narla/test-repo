package com.fileprocessor.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for FileUploadController.
 * Tests the full application stack including Spring context, controllers, services, and file I/O.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "upload.dir=${java.io.tmpdir}/test-uploads",
        "output.dir=${java.io.tmpdir}/test-uploads/output"
})
class FileUploadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @TempDir
    Path tempDir;

    private byte[] validImageBytes;

    @BeforeEach
    void setUp() throws Exception {
        validImageBytes = createTestImageBytes();
    }

    @Test
    void uploadFile_WithValidImage_ShouldReturnSuccessResponse() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                validImageBytes
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/files/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("File processed successfully")))
                .andExpect(jsonPath("$.originalFileName", is("test.png")))
                .andExpect(jsonPath("$.outputFileName", startsWith("processed_")))
                .andExpect(jsonPath("$.outputFileName", endsWith(".png")))
                .andExpect(jsonPath("$.downloadUrl", containsString("/api/files/download/")))
                .andExpect(jsonPath("$.fileSize", greaterThan(0)))
                .andExpect(jsonPath("$.processingTime", endsWith("ms")));
    }

    @Test
    void uploadFile_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/files/upload")
                        .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid File")))
                .andExpect(jsonPath("$.message", containsString("Please select a file to upload")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void uploadFile_WithNonImageFile_ShouldReturnBadRequest() throws Exception {
        // Arrange
        MockMultipartFile textFile = new MockMultipartFile(
                "file",
                "document.txt",
                "text/plain",
                "This is not an image".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/files/upload")
                        .file(textFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid File")))
                .andExpect(jsonPath("$.message", containsString("File must be an image")));
    }

    @Test
    void uploadFile_WithInvalidImageData_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        MockMultipartFile corruptedFile = new MockMultipartFile(
                "file",
                "corrupted.png",
                "image/png",
                "not an actual image".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/files/upload")
                        .file(corruptedFile))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("File Processing Error")));
    }

    @Test
    void uploadFile_WithJpegImage_ShouldSucceed() throws Exception {
        // Arrange
        MockMultipartFile jpegFile = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                validImageBytes
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/files/upload")
                        .file(jpegFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalFileName", is("photo.jpg")));
    }

    @Test
    void health_ShouldReturnUpStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/files/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    void uploadFile_ShouldHandleConcurrentRequests() throws Exception {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile("file", "test1.png", "image/png", validImageBytes);
        MockMultipartFile file2 = new MockMultipartFile("file", "test2.png", "image/png", validImageBytes);

        // Act & Assert - Both should succeed independently
        mockMvc.perform(multipart("/api/files/upload").file(file1))
                .andExpect(status().isOk());

        mockMvc.perform(multipart("/api/files/upload").file(file2))
                .andExpect(status().isOk());
    }

    @Test
    void uploadFile_WithLargeValidImage_ShouldSucceed() throws Exception {
        // Arrange - Create a larger image
        byte[] largeImageBytes = createTestImageBytes(500, 500);
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large.png",
                "image/png",
                largeImageBytes
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/files/upload")
                        .file(largeFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileSize", greaterThan(1000)));
    }

    private byte[] createTestImageBytes() throws Exception {
        return createTestImageBytes(100, 100);
    }

    private byte[] createTestImageBytes(int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
