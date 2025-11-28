package com.fileprocessor.processor;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Image processor that converts to grayscale and adds a border.
 * Implements Strategy pattern for image processing.
 */
@Component
public class GrayscaleBorderProcessor implements ImageProcessor {

    private static final int BORDER_SIZE = 20;
    private static final String WATERMARK_TEXT = "Processed";

    @Override
    public BufferedImage process(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage processed = createImageWithBorder(width, height);
        Graphics2D g2d = processed.createGraphics();

        try {
            drawBorder(g2d, width, height);
            BufferedImage grayscale = convertToGrayscale(original, width, height);
            drawImage(g2d, grayscale);
            addWatermark(g2d);
        } finally {
            g2d.dispose();
        }

        return processed;
    }

    private BufferedImage createImageWithBorder(int width, int height) {
        return new BufferedImage(
                width + 2 * BORDER_SIZE,
                height + 2 * BORDER_SIZE,
                BufferedImage.TYPE_INT_RGB
        );
    }

    private void drawBorder(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, width + 2 * BORDER_SIZE, height + 2 * BORDER_SIZE);
    }

    private BufferedImage convertToGrayscale(BufferedImage original, int width, int height) {
        BufferedImage grayscale = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D gGray = grayscale.createGraphics();
        try {
            gGray.drawImage(original, 0, 0, null);
        } finally {
            gGray.dispose();
        }
        return grayscale;
    }

    private void drawImage(Graphics2D g2d, BufferedImage image) {
        g2d.drawImage(image, BORDER_SIZE, BORDER_SIZE, null);
    }

    private void addWatermark(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString(WATERMARK_TEXT, BORDER_SIZE + 10, BORDER_SIZE + 30);
    }
}
