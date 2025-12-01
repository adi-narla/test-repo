package com.fileprocessor.processor;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Image processor that applies sepia tone effect.
 * Implements Strategy pattern for image processing.
 */
@Component("sepiaProcessor")
public class SepiaProcessor implements ImageProcessor {

    private static final int BORDER_SIZE = 15;
    private static final String WATERMARK_TEXT = "Sepia";

    @Override
    public BufferedImage process(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage processed = createImageWithBorder(width, height);
        Graphics2D g2d = processed.createGraphics();

        try {
            drawBorder(g2d, width, height);
            BufferedImage sepia = applySepiaTone(original, width, height);
            drawImage(g2d, sepia);
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
        g2d.setColor(new Color(101, 67, 33)); // Brown border for sepia
        g2d.fillRect(0, 0, width + 2 * BORDER_SIZE, height + 2 * BORDER_SIZE);
    }

    private BufferedImage applySepiaTone(BufferedImage original, int width, int height) {
        BufferedImage sepia = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = original.getRGB(x, y);
                Color color = new Color(rgb);

                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

                tr = Math.min(255, tr);
                tg = Math.min(255, tg);
                tb = Math.min(255, tb);

                Color sepiaColor = new Color(tr, tg, tb);
                sepia.setRGB(x, y, sepiaColor.getRGB());
            }
        }

        return sepia;
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
