package com.fileprocessor.processor;

/**
 * Enumeration of available image processing types.
 * Used to select the appropriate Strategy implementation.
 */
public enum ProcessorType {
    GRAYSCALE_BORDER("grayscaleBorderProcessor"),
    SEPIA("sepiaProcessor"),
    BLUR("blurProcessor");

    private final String beanName;

    ProcessorType(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
