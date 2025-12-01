# File Processor - Spring Boot & Angular Application

A full-stack web application for uploading and processing image files with multiple processing effects. Built with Spring Boot backend and Angular frontend, featuring authentication, multiple image processors, comprehensive testing, and Docker support.

## Features

- 📁 **File Upload** - Drag-and-drop support with client-side validation
- 🖼️ **Multiple Processing Effects** - Grayscale, Sepia, and Blur processors
- 🔒 **Authentication** - Spring Security with Basic Auth
- ✅ **File Validation** - Type and size validation (PNG, JPEG, GIF, WebP, max 10MB)
- 👁️ **Live Preview** - Preview images before processing
- ⬇️ **Download Results** - Download processed images
- 📚 **API Documentation** - Swagger/OpenAPI UI
- 🧪 **Comprehensive Tests** - Unit and integration tests
- 🐳 **Docker Support** - Containerized deployment
- 🎨 **Modern UI** - Responsive design with gradient backgrounds

## Tech Stack

### Backend
- **Spring Boot 3.2.0** - REST API framework
- **Spring Security** - Authentication and authorization
- **Java 17** - Programming language
- **Maven** - Build tool
- **JUnit 5 & Mockito** - Testing frameworks
- **Springdoc OpenAPI** - API documentation
- **TwelveMonkeys ImageIO** - Advanced image processing

### Frontend
- **Angular 17** - Frontend framework
- **TypeScript** - Type-safe JavaScript
- **RxJS** - Reactive programming
- **FormsModule** - Two-way data binding
- **Standalone Components** - Modern Angular architecture

## Prerequisites

### Option 1: Local Development
- **Java 17** or higher
- **Maven 3.6+**
- **Node.js 18+** and **npm**
- **Angular CLI** (optional: `npm install -g @angular/cli`)

### Option 2: Docker
- **Docker** 20.10+
- **Docker Compose** 2.0+

## Quick Start with Docker

The fastest way to test the application:

```bash
# Clone the repository (if not already)
cd test-repo

# Start both backend and frontend
docker-compose up --build

# Access the application
# Frontend: http://localhost:4200
# Backend API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

**Default Credentials:**
- Username: `user` / Password: `password` (USER role)
- Username: `admin` / Password: `admin123` (ADMIN role)

To stop:
```bash
docker-compose down
```

## Local Development Setup

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run tests (optional):
   ```bash
   mvn test
   ```

4. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory (from project root):
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

## Testing the Application

### 1. Access the Frontend
Open your browser and navigate to `http://localhost:4200`

### 2. Test File Upload
1. Select an image file (PNG, JPEG, GIF, or WebP, max 10MB)
2. Choose a processing effect from the dropdown:
   - **Grayscale with Border** - Converts to grayscale with dark gray border
   - **Sepia Tone** - Applies sepia effect with brown border
   - **Blur Effect** - Applies blur with blue border
3. Preview your selected image
4. Click "Upload and Process"
5. **Enter credentials** when prompted:
   - Username: `user`
   - Password: `password`
6. View the processed result
7. Download the processed image

### 3. Test API with Swagger UI
1. Navigate to `http://localhost:8080/swagger-ui.html`
2. Explore available endpoints
3. Click "Authorize" button and enter credentials
4. Try out API endpoints directly from the browser

### 4. Test API with cURL

**Health Check (no auth required):**
```bash
curl http://localhost:8080/api/files/health
```

**Upload File:**
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -u user:password \
  -F "file=@/path/to/your/image.jpg" \
  -F "processorType=SEPIA"
```

**Download Processed File:**
```bash
curl -X GET http://localhost:8080/api/files/download/processed_uuid.png \
  -u user:password \
  --output result.png
```

### 5. Run Unit Tests

**Backend Tests:**
```bash
cd backend
mvn test
```

Tests include:
- FileProcessingServiceTest
- GrayscaleBorderProcessorTest
- FileUploadControllerTest
- GlobalExceptionHandlerTest
- Integration tests with full Spring context

**Frontend Tests:**
```bash
cd frontend
npm test
```

## API Endpoints

### Authentication
All endpoints except `/health` and Swagger UI require Basic Authentication.

**Users:**
- `user` / `password` - USER role
- `admin` / `admin123` - ADMIN role

### POST `/api/files/upload`
Upload and process an image file.

**Request:**
- Method: `POST`
- Content-Type: `multipart/form-data`
- Auth: Required (Basic Auth)
- Parameters:
  - `file` (required) - Image file
  - `processorType` (optional) - `GRAYSCALE_BORDER`, `SEPIA`, or `BLUR` (default: GRAYSCALE_BORDER)

**Response:**
```json
{
  "message": "File processed successfully with SEPIA processor",
  "originalFileName": "image.jpg",
  "outputFileName": "processed_abc123.png",
  "downloadUrl": "/api/files/download/processed_abc123.png",
  "fileSize": 245678,
  "processingTime": "125ms"
}
```

### GET `/api/files/download/{filename}`
Download a processed image file.

**Request:**
- Method: `GET`
- Auth: Required (Basic Auth)
- Path Parameter: `filename` - Name of the processed file

**Response:**
- Content-Type: `image/png`
- Body: Image file

### GET `/api/files/health`
Health check endpoint (public, no auth required).

**Response:**
```json
{
  "status": "UP"
}
```

### GET `/swagger-ui.html`
Interactive API documentation (public, no auth required).

## Image Processing Effects

### Grayscale with Border (GRAYSCALE_BORDER)
- Converts image to grayscale
- Adds 20px dark gray border
- Adds "Processed" watermark

### Sepia Tone (SEPIA)
- Applies sepia color matrix transformation
- Adds 15px brown border
- Adds "Sepia" watermark

### Blur Effect (BLUR)
- Applies 3x3 convolution blur kernel
- Adds 10px steel blue border
- Adds "Blurred" watermark

## Configuration

### Backend Configuration
Edit `backend/src/main/resources/application.properties`:

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

### Frontend Configuration
Edit `frontend/src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### Modify Users
Edit `backend/src/main/java/com/fileprocessor/config/SecurityConfig.java` to add/modify users.

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
npm run build --configuration=production
# Output will be in frontend/dist/file-processor-frontend
```

### Docker Production Build
```bash
docker-compose build
docker-compose up -d
```

## Architecture & Design Patterns

The application follows SOLID principles and implements several design patterns:

- **Strategy Pattern** - Multiple image processors (GrayscaleBorderProcessor, SepiaProcessor, BlurProcessor)
- **Factory Pattern** - ImageProcessorFactory for processor instantiation
- **Builder Pattern** - DTOs use Lombok's @Builder
- **Dependency Injection** - Constructor injection with @RequiredArgsConstructor
- **Repository Pattern** - Service layer abstraction with interfaces
- **DTO Pattern** - Separate data transfer objects for API responses
- **Exception Handling** - Global exception handler with @RestControllerAdvice

## Testing

### Unit Tests
- **Service Layer** - FileProcessingServiceTest with Mockito
- **Processor Layer** - GrayscaleBorderProcessorTest
- **Controller Layer** - FileUploadControllerTest
- **Exception Handling** - GlobalExceptionHandlerTest

### Integration Tests
- **FileUploadControllerIntegrationTest** - Full stack tests with @SpringBootTest
- Tests multiple file formats, concurrent requests, and error scenarios

Run all tests:
```bash
cd backend
mvn test
```

## Troubleshooting

### Backend Issues

**Port 8080 already in use:**
```bash
# Find process using port 8080
lsof -i :8080
# Kill the process or change port in application.properties
```

**File upload fails:**
- Check `upload.dir` write permissions
- Verify file size is under 10MB
- Ensure file is a valid image format

**Authentication fails:**
- Verify credentials: `user/password` or `admin/admin123`
- Check browser hasn't cached old credentials

### Frontend Issues

**CORS errors:**
- Verify backend is running on port 8080
- Check CORS configuration in `CorsConfig.java`

**API connection failed:**
- Ensure backend is started before frontend
- Check console for error messages
- Verify API URL in `environment.ts`

**npm install fails:**
- Delete `node_modules` and `package-lock.json`
- Run `npm cache clean --force`
- Run `npm install` again

### Docker Issues

**Build fails:**
```bash
# Clean Docker cache
docker system prune -a
docker-compose build --no-cache
```

**Container won't start:**
```bash
# Check logs
docker-compose logs backend
docker-compose logs frontend
```

## Project Structure

```
test-repo/
├── backend/                         # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fileprocessor/
│   │   │   │   ├── FileProcessorApplication.java
│   │   │   │   ├── config/          # Configuration classes
│   │   │   │   │   ├── CorsConfig.java
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   └── OpenApiConfig.java
│   │   │   │   ├── controller/      # REST controllers
│   │   │   │   │   └── FileUploadController.java
│   │   │   │   ├── dto/            # Data transfer objects
│   │   │   │   │   ├── FileUploadResponse.java
│   │   │   │   │   └── ErrorResponse.java
│   │   │   │   ├── exception/       # Custom exceptions
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── FileProcessingException.java
│   │   │   │   │   └── InvalidFileException.java
│   │   │   │   ├── processor/       # Image processors
│   │   │   │   │   ├── ImageProcessor.java
│   │   │   │   │   ├── ImageProcessorFactory.java
│   │   │   │   │   ├── ProcessorType.java
│   │   │   │   │   ├── GrayscaleBorderProcessor.java
│   │   │   │   │   ├── SepiaProcessor.java
│   │   │   │   │   └── BlurProcessor.java
│   │   │   │   └── service/         # Business logic
│   │   │   │       ├── IFileProcessingService.java
│   │   │   │       └── FileProcessingService.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                    # Unit & integration tests
│   ├── uploads/                     # File storage (gitignored)
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                        # Angular frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   │   └── file-upload/
│   │   │   │       ├── file-upload.component.ts
│   │   │   │       ├── file-upload.component.html
│   │   │   │       └── file-upload.component.css
│   │   │   ├── services/
│   │   │   │   └── file-upload.service.ts
│   │   │   └── app.component.ts
│   │   ├── environments/
│   │   │   ├── environment.ts
│   │   │   └── environment.prod.ts
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.css
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
├── docker-compose.yml
├── .dockerignore
├── .gitignore
└── README.md
```

## License

MIT License

## Support

For issues or questions:
- Check the Troubleshooting section
- Review API documentation at `/swagger-ui.html`
- Check application logs in console
