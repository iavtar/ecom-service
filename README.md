# E-commerce Service

A modern, scalable e-commerce microservice built with Spring Boot, following clean architecture principles and best practices.

## 🚀 Quick Start

### Prerequisites
- **Java 21** (OpenJDK or Oracle JDK)
- **Maven 3.8+**
- **MySQL 8.0+** (for production)

### Running the Application

#### Development (Default - H2 Database)
```bash
mvn spring-boot:run -pl web
```

#### Development Profile (MySQL)
```bash
mvn spring-boot:run -pl web -Dspring-boot.run.profiles=dev
```

#### Production Profile
```bash
mvn spring-boot:run -pl web -Dspring-boot.run.profiles=prod
```

The application will start on `http://localhost:8080`

## 📁 Project Structure

```
ecom-service/
├── domain/           # Domain entities and business logic
├── infrastructure/   # Data access layer (JPA, repositories)
├── service/          # Business service layer
├── web/             # Web layer (REST controllers)
├── security/        # Security configuration
└── infrastructure/  # Infrastructure concerns
```

## 🏗️ Architecture

This project follows **Clean Architecture** principles with a modular structure:

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│     Web     │───▶│   Service   │───▶│Infrastructure│
│ (Controllers)│    │(Business    │    │(Repositories)│
│             │    │  Logic)     │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │                   │                   │
       ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Domain    │    │   Domain    │    │   Domain    │
│ (Entities)  │    │ (Entities)  │    │ (Entities)  │
└─────────────┘    └─────────────┘    └─────────────┘
```

### Module Dependencies
- **Web** → **Service** → **Infrastructure**
- **Web** → **Domain**
- **Service** → **Domain**
- **Infrastructure** → **Domain**

## 🗄️ Database Configuration

The application supports multiple database configurations:

### Development
- **Default**: H2 in-memory database
- **Dev Profile**: MySQL with `create-drop` DDL
- **Test Profile**: H2 in-memory for testing

### Production
- **MySQL** with environment variable configuration
- **DDL Auto**: `validate` (schema validation only)

### Environment Variables
```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=ecom_service
DB_USERNAME=root
DB_PASSWORD=toor
```

## 🔧 Configuration

### Environment-Specific Files
- `application.yml` - Base configuration
- `application-dev.yml` - Development profile
- `application-prod.yml` - Production profile
- `application-test.yml` - Test profile

### Key Configuration Features
- **Service Name**: `ecom-service`
- **Server Port**: 8080
- **Logging**: Custom pattern with service name prefix
- **JPA**: Optimized for performance and development

## 📊 API Endpoints

### User Management
```
POST   /api/users              # Create user
GET    /api/users              # Get all users
GET    /api/users/{id}         # Get user by ID
GET    /api/users/username/{username}  # Get user by username
PUT    /api/users/{id}         # Update user
DELETE /api/users/{id}         # Delete user
GET    /api/users/check-username/{username}  # Check username exists
```

### Sample Requests

#### Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "active": true
  }'
```

#### Get All Users
```bash
curl http://localhost:8080/api/users
```

## 🛠️ Development

### Building the Project
```bash
mvn clean install
```

### Running Tests
```bash
mvn test
```

### Code Quality
The project follows Spring Boot best practices and clean architecture principles.

## 📚 Documentation

- [Database Setup Guide](DATABASE_SETUP.md) - Complete database configuration
- [Environment Configuration](ENVIRONMENT_CONFIG.md) - Environment-specific settings
- [Docker Guide](DOCKER_GUIDE.md) - Docker setup and deployment

## 🔒 Security

Security module is included for future authentication and authorization features.

## 🚀 Deployment

### Docker
```bash
# Development (H2 Database)
docker-compose -f docker-compose.dev.yml up --build

# Development (MySQL)
docker-compose -f docker-compose.dev.yml --profile mysql up --build

# Production
docker-compose up --build
```

For detailed Docker instructions, see [Docker Guide](DOCKER_GUIDE.md).

### Production Checklist
- [ ] Set environment variables
- [ ] Configure MySQL database
- [ ] Set up monitoring and logging
- [ ] Configure security
- [ ] Set up CI/CD pipeline

## 🧪 Testing

### Test Profiles
- **Unit Tests**: JUnit 5 with Mockito
- **Integration Tests**: Spring Boot Test
- **Database Tests**: H2 in-memory

### Running Tests
```bash
# All tests
mvn test

# Specific module
mvn test -pl service

# With specific profile
mvn test -Dspring.profiles.active=test
```

## 📈 Monitoring & Logging

### Logging Configuration
- **Service Name**: `[ecom-service]` prefix
- **Log Level**: Configurable per environment
- **Format**: Structured logging with timestamps

### Sample Log Output
```
[ecom-service] 2025-07-27 10:30:15 [main] INFO  o.s.b.SpringApplication - Starting Application
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For issues and questions:
1. Check the documentation
2. Review existing issues
3. Create a new issue with detailed information

## 🔄 Version History

- **v1.0-SNAPSHOT**: Initial release with basic user management
  - Clean architecture implementation
  - Multi-module Maven project
  - Environment-specific configuration
  - RESTful API endpoints
  - Database integration (H2/MySQL)

---

**Built with ❤️ using Spring Boot 3.5.4 and Java 21** 