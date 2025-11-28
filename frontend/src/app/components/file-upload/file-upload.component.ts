import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FileUploadService, UploadResponse } from '../../services/file-upload.service';

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent {
  selectedFile: File | null = null;
  uploadStatus: string = '';
  isUploading: boolean = false;
  uploadResponse: UploadResponse | null = null;
  previewUrl: string | null = null;
  errorMessage: string = '';

  constructor(private fileUploadService: FileUploadService) {}

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.errorMessage = '';
      this.uploadResponse = null;

      // Create preview for the selected image
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.previewUrl = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  onUpload(): void {
    if (!this.selectedFile) {
      this.errorMessage = 'Please select a file first';
      return;
    }

    this.isUploading = true;
    this.uploadStatus = 'Uploading...';
    this.errorMessage = '';

    this.fileUploadService.uploadFile(this.selectedFile).subscribe({
      next: (response) => {
        this.isUploading = false;
        this.uploadStatus = 'Upload successful!';
        this.uploadResponse = response;
      },
      error: (error) => {
        this.isUploading = false;
        this.uploadStatus = '';
        this.errorMessage = error.error?.error || 'Failed to upload file';
        console.error('Upload error:', error);
      }
    });
  }

  getDownloadUrl(): string {
    if (this.uploadResponse?.outputFileName) {
      return this.fileUploadService.getDownloadUrl(this.uploadResponse.outputFileName);
    }
    return '';
  }

  reset(): void {
    this.selectedFile = null;
    this.uploadStatus = '';
    this.uploadResponse = null;
    this.previewUrl = null;
    this.errorMessage = '';
  }
}
