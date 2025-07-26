# Database Setup Guide

## Prerequisites

1. **MySQL Server** (version 8.0 or higher recommended)
2. **Java 21**
3. **Maven**

## Database Configuration

The application is configured with multiple profiles for different environments:

### Default Configuration
- **Database**: MySQL
- **Host**: localhost
- **Port**: 3306
- **Database Name**: ecom_service
- **Username**: root
- **Password**: password

### Profiles

#### Development Profile (`dev`)
- Database: `ecom_service_dev`
- DDL Auto: `create-drop` (recreates schema on startup)
- SQL Logging: Enabled

#### Production Profile (`prod`)
- Database: Uses environment variables
- DDL Auto: `validate` (validates schema)
- SQL Logging: Disabled

#### Test Profile (`test`)
- Database: H2 in-memory
- DDL Auto: `create-drop`
- SQL Logging: Enabled

## Setup Instructions

### 1. Install MySQL Server

#### Windows
```bash
# Download and install MySQL from https://dev.mysql.com/downloads/mysql/
# Or use Chocolatey:
choco install mysql
```

#### macOS
```bash
# Using Homebrew
brew install mysql
brew services start mysql
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 2. Create Database

Connect to MySQL and create the database:

```sql
-- Connect to MySQL as root
mysql -u root -p

-- Create database
CREATE DATABASE ecom_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create development database
CREATE DATABASE ecom_service_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (optional, for production)
CREATE USER 'ecom_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON ecom_service.* TO 'ecom_user'@'localhost';
GRANT ALL PRIVILEGES ON ecom_service_dev.* TO 'ecom_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Update Configuration (if needed)

Edit `web/src/main/resources/application.yml` to match your MySQL setup:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecom_service?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

### 4. Run the Application

#### Default Profile
```bash
mvn spring-boot:run -pl web
```

#### Development Profile
```bash
mvn spring-boot:run -pl web -Dspring-boot.run.profiles=dev
```

#### Production Profile
```bash
# Set environment variables
export DB_HOST=your_db_host
export DB_PORT=3306
export DB_NAME=ecom_service
export DB_USERNAME=your_username
export DB_PASSWORD=your_password

mvn spring-boot:run -pl web -Dspring-boot.run.profiles=prod
```

## Database Schema

The application will automatically create the following tables based on JPA entities:

- `user` - User accounts
- `profile` - User profiles
- `address` - User addresses

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure MySQL server is running
   - Check if port 3306 is accessible

2. **Access Denied**
   - Verify username and password
   - Check user privileges

3. **Database Not Found**
   - Create the database manually
   - Check database name in configuration

4. **SSL Issues**
   - The configuration includes `useSSL=false` to avoid SSL issues
   - For production, configure proper SSL certificates

### Useful Commands

```bash
# Check MySQL status
sudo systemctl status mysql

# Connect to MySQL
mysql -u root -p

# Show databases
SHOW DATABASES;

# Show tables
USE ecom_service;
SHOW TABLES;
```

## Environment Variables for Production

Set these environment variables for production deployment:

```bash
DB_HOST=your_database_host
DB_PORT=3306
DB_NAME=ecom_service
DB_USERNAME=your_database_user
DB_PASSWORD=your_database_password
```

## Security Considerations

1. **Never commit passwords** to version control
2. **Use environment variables** for production credentials
3. **Create dedicated database users** with minimal required privileges
4. **Enable SSL** for production database connections
5. **Regular backups** of your database 