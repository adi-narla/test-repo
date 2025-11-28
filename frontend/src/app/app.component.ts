import { Component } from '@angular/core';
import { FileUploadComponent } from './components/file-upload/file-upload.component';

/**
 * Root application component.
 * Provides the main application layout and file upload functionality.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FileUploadComponent],
  template: `
    <div class="container">
      <h1>File Processor</h1>
      <app-file-upload></app-file-upload>
    </div>
  `,
  styles: []
})
export class AppComponent {
  readonly title = 'File Processor';
}
