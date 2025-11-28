package com.fileprocessor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileProcessingService {

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${output.dir}")
    private String outputDir;

    public String processFile(MultipartFile file) throws IOException {
        // Create directories if they don't exist
        Path uploadPath = Paths.get(uploadDir);
        Path outputPath = Paths.get(outputDir);
        Files.createDirectories(uploadPath);
        Files.createDirectories(outputPath);

        // Save uploaded file temporarily
        String originalFileName = file.getOriginalFilename();
        String tempFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path tempFilePath = uploadPath.resolve(tempFileName);
        Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Process the image
        BufferedImage inputImage = ImageIO.read(tempFilePath.toFile());

        if (inputImage == null) {
            throw new IOException("Invalid image file");
        }

        // Apply image processing (example: add a border and convert to grayscale)
        BufferedImage outputImage = processImage(inputImage);

        // Save processed image
        String outputFileName = "processed_" + UUID.randomUUID().toString() + ".png";
        Path outputFilePath = outputPath.resolve(outputFileName);
        ImageIO.write(outputImage, "png", outputFilePath.toFile());

        // Clean up temporary file
        Files.deleteIfExists(tempFilePath);

        return outputFileName;
    }

    private BufferedImage processImage(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        int borderSize = 20;

        // Create new image with border
        BufferedImage processed = new BufferedImage(
                width + 2 * borderSize,
                height + 2 * borderSize,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = processed.createGraphics();

        // Draw border
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, width + 2 * borderSize, height + 2 * borderSize);

        // Convert to grayscale and draw
        BufferedImage grayscale = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D gGray = grayscale.createGraphics();
        gGray.drawImage(original, 0, 0, null);
        gGray.dispose();

        g2d.drawImage(grayscale, borderSize, borderSize, null);

        // Add watermark text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Processed", borderSize + 10, borderSize + 30);

        g2d.dispose();

        return processed;
    }

    public Path getOutputFile(String filename) {
        return Paths.get(outputDir).resolve(filename);
    }
}
