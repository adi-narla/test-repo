package com.fileprocessor.exception;

/**
 * Custom exception for file storage related errors.
 * Used when file save/read operations fail.
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
