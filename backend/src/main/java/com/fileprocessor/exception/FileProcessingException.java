package com.fileprocessor.exception;

/**
 * Custom exception for file processing errors.
 * Extends RuntimeException for unchecked exception handling.
 */
public class FileProcessingException extends RuntimeException {

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
