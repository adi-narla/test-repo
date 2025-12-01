package com.fileprocessor.processor;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Image processor that applies blur effect.
 * Implements Strategy pattern for image processing.
 */
@Component("blurProcessor")
public class BlurProcessor implements ImageProcessor {

    private static final int BORDER_SIZE = 10;
    private static final String WATERMARK_TEXT = "Blurred";

    @Override
    public BufferedImage process(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage processed = createImageWithBorder(width, height);
        Graphics2D g2d = processed.createGraphics();

        try {
            drawBorder(g2d, width, height);
            BufferedImage blurred = applyBlur(original);
            drawImage(g2d, blurred);
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
        g2d.setColor(new Color(70, 130, 180)); // Steel blue border
        g2d.fillRect(0, 0, width + 2 * BORDER_SIZE, height + 2 * BORDER_SIZE);
    }

    private BufferedImage applyBlur(BufferedImage original) {
        // Create 3x3 blur kernel
        float[] blurKernel = {
                1/9f, 1/9f, 1/9f,
                1/9f, 1/9f, 1/9f,
                1/9f, 1/9f, 1/9f
        };

        Kernel kernel = new Kernel(3, 3, blurKernel);
        ConvolveOp convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        return convolveOp.filter(original, null);
    }

    private void drawImage(Graphics2D g2d, BufferedImage image) {
        g2d.drawImage(image, BORDER_SIZE, BORDER_SIZE, null);
    }

    private void addWatermark(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(WATERMARK_TEXT, BORDER_SIZE + 10, BORDER_SIZE + 25);
    }
}
