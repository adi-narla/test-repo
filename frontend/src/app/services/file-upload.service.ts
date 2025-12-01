import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { environment } from '../../environments/environment';

/**
 * Interface for file upload response from backend.
 * Matches the FileUploadResponse DTO from Spring Boot.
 */
export interface UploadResponse {
  message: string;
  originalFileName: string;
  outputFileName: string;
  downloadUrl: string;
  fileSize?: number;
  processingTime?: string;
}

/**
 * Interface for error responses from backend.
 * Matches the ErrorResponse DTO structure.
 */
export interface ErrorResponse {
  error: string;
  message: string;
  status: number;
  timestamp: string;
  path: string;
}

/**
 * Service for handling file upload operations.
 * Provides methods for uploading files and retrieving processed results.
 * Uses environment configuration for API base URL.
 */
@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  private readonly apiUrl = `${environment.apiUrl}/files`;
  private readonly MAX_RETRIES = 2;

  constructor(private readonly http: HttpClient) {}

  /**
   * Upload a file to the backend for processing.
   * Automatically retries failed requests up to MAX_RETRIES times.
   *
   * @param file The file to upload
   * @param processorType Optional processor type (default: GRAYSCALE_BORDER)
   * @returns Observable of UploadResponse
   */
  uploadFile(file: File, processorType: string = 'GRAYSCALE_BORDER'): Observable<UploadResponse> {
    this.validateFile(file);

    const formData = new FormData();
    formData.append('file', file);
    formData.append('processorType', processorType);

    return this.http.post<UploadResponse>(`${this.apiUrl}/upload`, formData).pipe(
      retry(this.MAX_RETRIES),
      catchError(this.handleError)
    );
  }

  /**
   * Get the download URL for a processed file.
   *
   * @param filename The name of the processed file
   * @returns Complete URL for downloading the file
   */
  getDownloadUrl(filename: string): string {
    return `${this.apiUrl}/download/${filename}`;
  }

  /**
   * Validate file before upload.
   * Throws error if file is invalid.
   */
  private validateFile(file: File): void {
    if (!file) {
      throw new Error('No file provided');
    }
    if (!file.type.startsWith('image/')) {
      throw new Error('File must be an image');
    }
  }

  /**
   * Handle HTTP errors with user-friendly messages.
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      const backendError = error.error as ErrorResponse;
      errorMessage = backendError?.message || `Server error: ${error.status}`;
    }

    return throwError(() => new Error(errorMessage));
  }
}
