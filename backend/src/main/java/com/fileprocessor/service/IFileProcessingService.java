package com.fileprocessor.service;

import com.fileprocessor.processor.ProcessorType;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Interface for file processing operations.
 * Follows Dependency Inversion Principle (SOLID).
 */
public interface IFileProcessingService {

    /**
     * Process an uploaded file using default processor and generate output image.
     *
     * @param file the uploaded multipart file
     * @return the filename of the processed output
     */
    String processFile(MultipartFile file);

    /**
     * Process an uploaded file using specified processor type and generate output image.
     *
     * @param file the uploaded multipart file
     * @param processorType the type of processor to use
     * @return the filename of the processed output
     */
    String processFile(MultipartFile file, ProcessorType processorType);

    /**
     * Retrieve the path to a processed output file.
     *
     * @param filename the output filename
     * @return path to the output file
     */
    Path getOutputFile(String filename);
}
