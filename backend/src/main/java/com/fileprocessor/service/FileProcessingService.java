package com.fileprocessor.service;

import com.fileprocessor.exception.FileProcessingException;
import com.fileprocessor.exception.FileStorageException;
import com.fileprocessor.exception.InvalidFileException;
import com.fileprocessor.processor.ImageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Implementation of file processing service.
 * Handles file upload, image processing, and storage operations.
 * Uses Strategy pattern through ImageProcessor for flexible processing algorithms.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessingService implements IFileProcessingService {

    private final ImageProcessor imageProcessor;

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${output.dir}")
    private String outputDir;

    @Override
    public String processFile(MultipartFile file) {
        validateFile(file);
        ensureDirectoriesExist();

        Path tempFilePath = null;
        try {
            tempFilePath = saveTemporaryFile(file);
            BufferedImage inputImage = readImage(tempFilePath);
            BufferedImage processedImage = imageProcessor.process(inputImage);
            String outputFileName = saveProcessedImage(processedImage);

            log.info("Successfully processed file: {} -> {}", file.getOriginalFilename(), outputFileName);
            return outputFileName;
        } catch (IOException e) {
            log.error("Failed to process file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Failed to process file", e);
        } finally {
            cleanupTemporaryFile(tempFilePath);
        }
    }

    @Override
    public Path getOutputFile(String filename) {
        return Paths.get(outputDir).resolve(filename);
    }

    private void validateFile(MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new InvalidFileException("File must be an image");
        }
    }

    private void ensureDirectoriesExist() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            Files.createDirectories(Paths.get(outputDir));
        } catch (IOException e) {
            throw new FileStorageException("Failed to create storage directories", e);
        }
    }

    private Path saveTemporaryFile(MultipartFile file) throws IOException {
        String tempFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path tempFilePath = Paths.get(uploadDir).resolve(tempFileName);
        Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        return tempFilePath;
    }

    private BufferedImage readImage(Path filePath) throws IOException {
        BufferedImage image = ImageIO.read(filePath.toFile());
        if (image == null) {
            throw new InvalidFileException("Invalid or unsupported image format");
        }
        return image;
    }

    private String saveProcessedImage(BufferedImage image) throws IOException {
        String outputFileName = "processed_" + UUID.randomUUID() + ".png";
        Path outputFilePath = Paths.get(outputDir).resolve(outputFileName);
        boolean success = ImageIO.write(image, "png", outputFilePath.toFile());

        if (!success) {
            throw new FileProcessingException("Failed to write processed image");
        }
        return outputFileName;
    }

    private void cleanupTemporaryFile(Path tempFilePath) {
        if (tempFilePath != null) {
            try {
                Files.deleteIfExists(tempFilePath);
            } catch (IOException e) {
                log.warn("Failed to delete temporary file: {}", tempFilePath, e);
            }
        }
    }
}
