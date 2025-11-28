package com.fileprocessor.processor;

import java.awt.image.BufferedImage;

/**
 * Interface for image processing operations.
 * Follows Strategy pattern for flexible image processing algorithms.
 */
public interface ImageProcessor {

    /**
     * Process a BufferedImage and return the transformed result.
     *
     * @param original the original image
     * @return the processed image
     */
    BufferedImage process(BufferedImage original);
}
