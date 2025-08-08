# 🏠 Parser Engine

A robust Spring Boot application for parsing and processing property listing files with secure authentication, AWS S3
integration, and comprehensive property management capabilities.

## 📋 Table of Contents

- [Overview](#Overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Deployment](#deployment)
- [Development](#development)
- [Contributing](#contributing)

## 🎯 Overview

Parser Engine is a comprehensive property management system that allows users to:

- Upload and process Excel files containing property data
- Search and filter properties with advanced criteria
- Manage user authentication with JWT tokens
- Store files securely in AWS S3
- Process property data with automated Excel parsing

The application is built with enterprise-grade security, comprehensive validation, and follows RESTful API design
principles.

## ✨ Features

### 🔐 Authentication & Security

- **JWT-based authentication** with access tokens (15 min) and refresh tokens (3 days)
- **Secure refresh token flow** requiring both access and refresh tokens
- **Password validation** with strong requirements (uppercase, lowercase, digits, special characters)
- **Token rotation** for enhanced security
- **Role-based access control**
- **Comprehensive error handling** with custom exception codes

### 📁 File Management

- **Excel file upload** to AWS S3
- **Automated Excel parsing** with header-based data extraction
- **File processing status tracking** (PENDING, COMPLETED, FAILED)
- **Soft delete** with audit trail
- **File search and filtering**
- **Download files** with proper content disposition

### 🏠 Property Management

- **Advanced property search** with multiple filters:
    - BHK/RK count
    - Location
    - Floor
    - Furnishing status
    - Area
    - Quoted amount
    - Car parking slots
- **Pagination support**
- **Property data import** from Excel files
- **Comprehensive property entity** with all necessary fields

### 🛠 Technical Features

- **JPA Auditing** with automatic user tracking
- **India timezone support** (UTC+5:30) throughout the application
- **Comprehensive validation** across all endpoints
- **Swagger/OpenAPI documentation**
- **Docker support** with multi-stage builds
- **Environment-specific configurations**
- **CORS configuration** for frontend integration

## 🛠 Tech Stack

### Backend

- **Java 17** with Spring Boot 3.5.3
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **MySQL 8.0** database
- **AWS SDK v2** for S3 integration
- **Apache POI** for Excel processing
- **MapStruct** for object mapping
- **Lombok** for boilerplate reduction

### Documentation & Testing

- **SpringDoc OpenAPI** (Swagger UI)
- **Spring Boot Test** with AssertJ
- **Logback** with JSON formatting

### DevOps

- **Docker** with multi-stage builds
- **Docker Compose** for local development
- **Maven** for dependency management

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0** or higher
- **Docker** and **Docker Compose** (optional, for containerized setup)
- **AWS Account** with S3 bucket (for file storage)

## 🚀 Quick Start

### Option 1: Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd parser-engine
   ```

2. **Set up MySQL database**
   ```bash
   # Using Docker Compose (recommended)
   docker-compose up -d mysql
   
   # Or manually create database
   mysql -u root -p
   CREATE DATABASE property_db;
   ```

3. **Configure application properties**
   ```bash
   # Copy and modify the configuration files
   cp src/main/resources/config/application-dev.yml.example src/main/resources/config/application-dev.yml
   ```

4. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

5. **Access the application**
    - **API Base URL**: `http://localhost:8080/parser-engine`
    - **Swagger UI**: `http://localhost:8080/parser-engine/swagger-ui/index.html`
    - **Health Check**: `http://localhost:8080/parser-engine/actuator/health`

### Option 2: Docker Deployment

1. **Build the Docker image**
   ```bash
   docker build -t parser-engine:latest .
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

## 📚 API Documentation

### Authentication Endpoints

#### 🔐 Sign Up

```http
POST /parser-engine/auth/v1/signup
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### 🔑 Login

```http
POST /parser-engine/auth/v1/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

#### 🔄 Refresh Token

```http
POST /parser-engine/auth/v1/refresh
X-Refresh-Token: <refresh-token>
Authorization: Bearer <expired-access-token>
```

#### 👤 Update Profile

```http
PATCH /parser-engine/auth/v1/update-profile
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com"
}
```

#### 🔒 Change Password

```http
PATCH /parser-engine/auth/v1/change-password
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "currentPassword": "OldPass123!",
  "newPassword": "NewSecurePass456!"
}
```

#### 🚪 Logout

```http
POST /parser-engine/auth/v1/logout
Authorization: Bearer <access-token>
```

#### 🗑️ Delete Account

```http
POST /parser-engine/auth/v1/delete-account
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "password": "SecurePass123!"
}
```

### File Management Endpoints

#### 📤 Upload File

```http
POST /parser-engine/wb/aws/v1/upload
Authorization: Bearer <access-token>
Content-Type: multipart/form-data

file: <excel-file>
```

#### 📥 Download File

```http
GET /parser-engine/wb/aws/v1/download/{fileId}
Authorization: Bearer <access-token>
```

#### 🔍 Search Files

```http
GET /parser-engine/wb/v1/files?fileName=properties&fileType=EXCEL&page=0&size=10
Authorization: Bearer <access-token>
```

#### ⚙️ Process File

```http
POST /parser-engine/wb/v1/files/{fileId}/process
Authorization: Bearer <access-token>
```

#### 🗑️ Delete File

```http
PATCH /parser-engine/wb/v1/files/{fileId}/delete
Authorization: Bearer <access-token>
```

### Property Management Endpoints

#### 🔍 Search Properties

```http
GET /parser-engine/wb/v1/properties?location=Mumbai&numberOfBhk=2&furnishingStatus=FURNISHED&page=0&size=20
Authorization: Bearer <access-token>
```

#### 📋 Get Property Details

```http
GET /parser-engine/wb/v1/properties/{propertyId}
Authorization: Bearer <access-token>
```

## ⚙️ Configuration

### Environment Variables

#### Development (`application-dev.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/property_db
    username: your-mysql-username
    password: your-mysql-password

aws:
  s3:
    region: ap-south-1
    bucket_name: your-bucket-name
    access_key: your-access-key
    secret_key: your-secret-key

app:
  jwt:
    secret: your-jwt-secret-key
    expiration-seconds: 900
    refresh-token-expiration-seconds: 259200
```

#### Production (`application-prod.yml`)

```yaml
spring:
  datasource:
    url: ${MYSQL_URL}
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}

aws:
  s3:
    region: ${AWS_REGION}
    bucket_name: ${S3_BUCKET_NAME}
    access_key: ${AWS_ACCESS_KEY}
    secret_key: ${AWS_SECRET_KEY}
```

### JWT Configuration

- **Access Token Expiration**: 15 minutes
- **Refresh Token Expiration**: 3 days
- **Token Prefix**: Bearer
- **Header Name**: Authorization

### Database Configuration

- **Timezone**: Asia/Kolkata (UTC+5:30)
- **Character Set**: UTF-8
- **Connection Pool**: HikariCP (default)

## 🚀 Deployment

### AWS Deployment

1. **Set up AWS resources**
    - Create S3 bucket for file storage
    - Configure IAM roles and permissions
    - Set up RDS MySQL instance

2. **Configure environment variables**
   ```bash
   export MYSQL_URL=jdbc:mysql://your-rds-endpoint:3306/property_db
   export AWS_ACCESS_KEY=your-access-key
   export AWS_SECRET_KEY=your-secret-key
   export S3_BUCKET_NAME=your-bucket-name
   ```

3. **Deploy using Docker**
   ```bash
   docker build -t parser-engine:latest .
   docker run -d -p 8080:8080 --env-file .env parser-engine:latest
   ```

### Docker Compose Production

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - MYSQL_URL=jdbc:mysql://db:3306/property_db
    depends_on:
      - db

  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: property_db
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

## 🛠 Development

### Project Structure

```
src/main/java/com/parser/engine/
├── common/           # Constants and common utilities
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dao/            # Data access objects
├── dto/            # Data transfer objects
├── entity/         # JPA entities
├── enums/          # Enumerations
├── exception/      # Custom exceptions
├── filter/         # Security filters
├── helper/         # Utility helpers
├── mapper/         # Object mappers
├── repository/     # JPA repositories
├── service/        # Business logic services
├── spec/           # JPA specifications
├── utils/          # Utility classes
└── validation/     # Custom validators
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthControllerTest
```

### Code Quality

```bash
# Check code style
mvn checkstyle:check

# Run SonarQube analysis
mvn sonar:sonar
```

## 🔧 Troubleshooting

### Common Issues

1. **Database Connection Issues**
    - Verify MySQL is running and accessible
    - Check database credentials in configuration
    - Ensure database exists: `CREATE DATABASE property_db;`

2. **AWS S3 Issues**
    - Verify AWS credentials are correct
    - Check S3 bucket exists and is accessible
    - Ensure proper IAM permissions

3. **JWT Token Issues**
    - Verify JWT secret is properly configured
    - Check token expiration settings
    - Ensure proper Authorization header format

4. **File Upload Issues**
    - Check file size limits (default: 10MB)
    - Verify file format is supported (Excel files)
    - Ensure AWS S3 bucket permissions

### Logs

Application logs are configured with Logback and can be found in:

- **Console**: Standard output
- **File**: `logs/parser-engine.log` (if configured)
- **JSON Format**: Structured logging for production

## 🤝 Contributing

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Development Guidelines

- Follow Java coding conventions
- Write unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages
- Ensure all tests pass before submitting PR

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Kunal Ladhani**

- Email: k.ladhani1@gmail.com
- GitHub: [@kunal-ladhani](https://github.com/kunal-ladhani)
- Website: [https://kunal-ladhani.github.io](https://kunal-ladhani.github.io)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- AWS team for comprehensive SDK
- Apache POI team for Excel processing capabilities
- All contributors and maintainers

---

**Note**: This application is designed for property data parsing and management. Ensure you have proper data privacy and
security measures in place when handling sensitive property information.