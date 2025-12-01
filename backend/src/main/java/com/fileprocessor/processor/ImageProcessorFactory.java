package com.fileprocessor.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Factory for creating ImageProcessor instances based on ProcessorType.
 * Implements Factory pattern combined with Strategy pattern.
 */
@Component
@RequiredArgsConstructor
public class ImageProcessorFactory {

    private final ApplicationContext applicationContext;

    /**
     * Get an ImageProcessor implementation based on the specified type.
     * Uses Spring's ApplicationContext to retrieve the appropriate bean.
     *
     * @param type the processor type
     * @return the corresponding ImageProcessor implementation
     * @throws IllegalArgumentException if the processor type is not found
     */
    public ImageProcessor getProcessor(ProcessorType type) {
        try {
            return applicationContext.getBean(type.getBeanName(), ImageProcessor.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid processor type: " + type, e);
        }
    }
}
