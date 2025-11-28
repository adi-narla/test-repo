import { Component, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { FileUploadService, UploadResponse } from '../../services/file-upload.service';

/**
 * Component for handling file upload and processing.
 * Provides UI for selecting files, uploading, and viewing processed results.
 * Implements OnDestroy for proper subscription cleanup.
 */
@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnDestroy {
  selectedFile: File | null = null;
  uploadStatus: string = '';
  isUploading: boolean = false;
  uploadResponse: UploadResponse | null = null;
  previewUrl: string | null = null;
  errorMessage: string = '';

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly fileUploadService: FileUploadService) {}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Handle file selection from input.
   * Creates a preview of the selected image.
   */
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (file) {
      this.selectedFile = file;
      this.errorMessage = '';
      this.uploadResponse = null;
      this.createPreview(file);
    }
  }

  /**
   * Upload the selected file to the backend.
   * Handles success and error responses.
   */
  onUpload(): void {
    if (!this.selectedFile) {
      this.errorMessage = 'Please select a file first';
      return;
    }

    this.startUpload();

    this.fileUploadService
      .uploadFile(this.selectedFile)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => this.handleUploadSuccess(response),
        error: (error) => this.handleUploadError(error)
      });
  }

  /**
   * Get the download URL for the processed file.
   */
  getDownloadUrl(): string {
    return this.uploadResponse?.outputFileName
      ? this.fileUploadService.getDownloadUrl(this.uploadResponse.outputFileName)
      : '';
  }

  /**
   * Reset the component state to initial values.
   */
  reset(): void {
    this.selectedFile = null;
    this.uploadStatus = '';
    this.uploadResponse = null;
    this.previewUrl = null;
    this.errorMessage = '';
  }

  /**
   * Create a preview URL for the selected image file.
   */
  private createPreview(file: File): void {
    const reader = new FileReader();
    reader.onload = (e: ProgressEvent<FileReader>) => {
      this.previewUrl = e.target?.result as string;
    };
    reader.onerror = () => {
      this.errorMessage = 'Failed to load image preview';
    };
    reader.readAsDataURL(file);
  }

  /**
   * Set upload state to in-progress.
   */
  private startUpload(): void {
    this.isUploading = true;
    this.uploadStatus = 'Uploading...';
    this.errorMessage = '';
  }

  /**
   * Handle successful upload response.
   */
  private handleUploadSuccess(response: UploadResponse): void {
    this.isUploading = false;
    this.uploadStatus = 'Upload successful!';
    this.uploadResponse = response;
  }

  /**
   * Handle upload error.
   */
  private handleUploadError(error: Error): void {
    this.isUploading = false;
    this.uploadStatus = '';
    this.errorMessage = error.message || 'Failed to upload file';
    console.error('Upload error:', error);
  }
}
