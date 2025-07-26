# Environment-Specific Configuration

This project uses Spring Boot's profile-based configuration with separate YAML files for different environments.

## Configuration Files

### 1. `application.yml` (Base Configuration)
- **Purpose**: Common configuration shared across all environments
- **Database**: H2 in-memory (default fallback)
- **DDL Auto**: `update`
- **SQL Logging**: Enabled

### 2. `application-dev.yml` (Development Profile)
- **Purpose**: Development environment configuration
- **Database**: H2 in-memory (`ecom_service_dev`)
- **DDL Auto**: `create-drop` (recreates schema on startup)
- **SQL Logging**: Enabled
- **Features**: 
  - Fresh database on each startup
  - Detailed SQL logging
  - Fast development cycle

### 3. `application-prod.yml` (Production Profile)
- **Purpose**: Production environment configuration
- **Database**: MySQL (configurable via environment variables)
- **DDL Auto**: `validate` (validates schema, doesn't modify)
- **SQL Logging**: Disabled
- **Environment Variables**:
  - `DB_HOST`: Database host (default: localhost)
  - `DB_PORT`: Database port (default: 3306)
  - `DB_NAME`: Database name (default: ecom_service)
  - `DB_USERNAME`: Database username (default: root)
  - `DB_PASSWORD`: Database password (default: toor)

### 4. `application-test.yml` (Test Profile)
- **Purpose**: Testing environment configuration
- **Database**: H2 in-memory (`testdb`)
- **DDL Auto**: `create-drop`
- **SQL Logging**: Enabled
- **Features**:
  - Isolated test database
  - Clean state for each test

## How to Run

### Development (Default)
```bash
mvn spring-boot:run -pl web
```

### Development Profile
```bash
mvn spring-boot:run -pl web -Dspring-boot.run.profiles=dev
```

### Production Profile
```bash
# Set environment variables
export DB_HOST=your_production_host
export DB_PORT=3306
export DB_NAME=ecom_service_prod
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_secure_password

mvn spring-boot:run -pl web -Dspring-boot.run.profiles=prod
```

### Test Profile
```bash
mvn spring-boot:run -pl web -Dspring-boot.run.profiles=test
```

## Environment Variables for Production

Create a `.env` file or set environment variables:

```bash
# Database Configuration
DB_HOST=your-mysql-server.com
DB_PORT=3306
DB_NAME=ecom_service
DB_USERNAME=ecom_user
DB_PASSWORD=secure_password_here

# Application Configuration
SERVER_PORT=8080
```

## Database Setup

### For Development (H2)
- No setup required - H2 creates in-memory database automatically
- Tables are created from JPA entities on startup

### For Production (MySQL)
1. Install MySQL Server
2. Create database:
   ```sql
   CREATE DATABASE ecom_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Create user (optional):
   ```sql
   CREATE USER 'ecom_user'@'%' IDENTIFIED BY 'secure_password';
   GRANT ALL PRIVILEGES ON ecom_service.* TO 'ecom_user'@'%';
   FLUSH PRIVILEGES;
   ```

## Profile Selection

### IntelliJ IDEA
1. Edit Run Configuration
2. Add VM Options: `-Dspring.profiles.active=dev`
3. Or add Environment Variables: `SPRING_PROFILES_ACTIVE=dev`

### Command Line
```bash
# Using system property
java -Dspring.profiles.active=dev -jar web/target/web-1.0-SNAPSHOT.jar

# Using environment variable
export SPRING_PROFILES_ACTIVE=dev
java -jar web/target/web-1.0-SNAPSHOT.jar
```

## Benefits of This Setup

1. **Environment Isolation**: Each environment has its own configuration
2. **Security**: Production credentials are externalized
3. **Flexibility**: Easy to switch between environments
4. **Development Speed**: H2 for fast development cycles
5. **Production Ready**: MySQL for production stability

## Troubleshooting

### Common Issues

1. **Profile Not Active**
   - Check if profile is correctly specified
   - Verify file naming: `application-{profile}.yml`

2. **Database Connection Issues**
   - Development: Check if H2 dependency is included
   - Production: Verify MySQL is running and accessible

3. **Environment Variables Not Picked Up**
   - Ensure variables are set before application startup
   - Check variable naming (case-sensitive) 