package com.fileprocessor.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GrayscaleBorderProcessor.
 * Tests image processing logic and output validation.
 */
class GrayscaleBorderProcessorTest {

    private GrayscaleBorderProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new GrayscaleBorderProcessor();
    }

    @Test
    void process_WithValidImage_ShouldAddBorderAndConvertToGrayscale() {
        // Arrange
        int originalWidth = 100;
        int originalHeight = 100;
        BufferedImage original = createColoredImage(originalWidth, originalHeight, Color.RED);

        // Act
        BufferedImage result = processor.process(original);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(originalWidth + 40); // 20px border on each side
        assertThat(result.getHeight()).isEqualTo(originalHeight + 40);
    }

    @Test
    void process_ShouldPreserveImageDimensions() {
        // Arrange
        BufferedImage original = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);

        // Act
        BufferedImage result = processor.process(original);

        // Assert
        assertThat(result.getWidth()).isEqualTo(240); // 200 + 2*20
        assertThat(result.getHeight()).isEqualTo(190); // 150 + 2*20
    }

    @Test
    void process_WithSmallImage_ShouldStillWork() {
        // Arrange
        BufferedImage original = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        // Act
        BufferedImage result = processor.process(original);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(50); // 10 + 2*20
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void process_WithLargeImage_ShouldHandleCorrectly() {
        // Arrange
        BufferedImage original = new BufferedImage(1000, 800, BufferedImage.TYPE_INT_RGB);

        // Act
        BufferedImage result = processor.process(original);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(1040);
        assertThat(result.getHeight()).isEqualTo(840);
    }

    @Test
    void process_ShouldReturnRGBImage() {
        // Arrange
        BufferedImage original = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        // Act
        BufferedImage result = processor.process(original);

        // Assert
        assertThat(result.getType()).isEqualTo(BufferedImage.TYPE_INT_RGB);
    }

    private BufferedImage createColoredImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return image;
    }
}
