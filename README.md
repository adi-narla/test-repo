# File Processor - Spring Boot & Angular Application

A full-stack web application for uploading and processing image files. Built with Spring Boot backend and Angular frontend.

## Features

- 📁 File upload with drag-and-drop support
- 🖼️ Image processing (grayscale conversion with border)
- 👁️ Live preview of uploaded images
- ⬇️ Download processed images
- 🎨 Modern, responsive UI

## Tech Stack

### Backend
- **Spring Boot 3.2.0**
- **Java 17**
- **Maven**
- Image processing with Java AWT and TwelveMonkeys ImageIO

### Frontend
- **Angular 17**
- **TypeScript**
- **RxJS**
- **Standalone Components**

## Project Structure

```
test-repo/
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   └── main/
│   │       ├── java/com/fileprocessor/
│   │       │   ├── FileProcessorApplication.java
│   │       │   ├── controller/
│   │       │   │   └── FileUploadController.java
│   │       │   ├── service/
│   │       │   │   └── FileProcessingService.java
│   │       │   └── config/
│   │       │       └── CorsConfig.java
│   │       └── resources/
│   │           └── application.properties
│   ├── uploads/                # Uploaded files directory
│   │   └── output/            # Processed images directory
│   └── pom.xml
├── frontend/                   # Angular frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   │   └── file-upload/
│   │   │   ├── services/
│   │   │   │   └── file-upload.service.ts
│   │   │   └── app.component.ts
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.css
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
└── README.md
```

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Node.js 18+** and **npm**
- **Angular CLI** (install with `npm install -g @angular/cli`)

## Setup Instructions

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:4200`

## Usage

1. Open your browser and navigate to `http://localhost:4200`
2. Click on "Choose an image file" or drag and drop an image
3. Preview your selected image
4. Click "Upload and Process" to process the image
5. View the processed image with grayscale effect and border
6. Download the processed image

## API Endpoints

### POST `/api/files/upload`
Upload and process a file.

**Request:**
- Method: `POST`
- Content-Type: `multipart/form-data`
- Body: Form data with `file` field

**Response:**
```json
{
  "message": "File processed successfully",
  "originalFileName": "image.jpg",
  "outputFileName": "processed_uuid.png",
  "downloadUrl": "/api/files/download/processed_uuid.png"
}
```

### GET `/api/files/download/{filename}`
Download a processed file.

**Request:**
- Method: `GET`
- Path Parameter: `filename` - Name of the processed file

**Response:**
- Content-Type: `image/png`
- Body: Image file

### GET `/api/files/health`
Health check endpoint.

**Response:**
```json
{
  "status": "UP"
}
```

## Image Processing

The application processes uploaded images by:
1. Converting to grayscale
2. Adding a dark gray border (20px)
3. Adding a "Processed" watermark
4. Saving as PNG format

## Configuration

Backend configuration can be modified in `backend/src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# File upload limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Upload directories
upload.dir=uploads
output.dir=uploads/output
```

Frontend API URL can be modified in `frontend/src/app/services/file-upload.service.ts`:

```typescript
private apiUrl = 'http://localhost:8080/api/files';
```

## Development

### Backend Development
- The backend uses Spring Boot DevTools for hot reloading
- Modify code in `backend/src/main/java/com/fileprocessor/`
- Logs will appear in the console

### Frontend Development
- Angular CLI provides hot module replacement
- Modify code in `frontend/src/app/`
- Browser will automatically reload

## Build for Production

### Backend
```bash
cd backend
mvn clean package
java -jar target/file-processor-backend-1.0.0.jar
```

### Frontend
```bash
cd frontend
npm run build
# Output will be in frontend/dist/file-processor-frontend
```

## Troubleshooting

### Backend Issues
- **Port already in use:** Change `server.port` in `application.properties`
- **File upload fails:** Check `upload.dir` permissions and `max-file-size` limit

### Frontend Issues
- **CORS errors:** Verify backend CORS configuration in `CorsConfig.java`
- **API connection failed:** Ensure backend is running on port 8080

## License

MIT License
