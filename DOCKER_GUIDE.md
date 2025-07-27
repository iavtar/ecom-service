# Docker Guide

This guide explains how to use Docker with the e-commerce service for development and production environments.

## 🐳 Docker Setup

### Prerequisites
- **Docker** (version 20.10+)
- **Docker Compose** (version 2.0+)

### Files Overview
- `Dockerfile` - Multi-stage build for the application
- `docker-compose.yml` - Production setup with MySQL and Redis
- `docker-compose.dev.yml` - Development setup with H2 database
- `.dockerignore` - Optimizes build context

## 🚀 Quick Start

### Development (H2 Database)
```bash
# Build and run with H2 database
docker-compose -f docker-compose.dev.yml up --build

# Access the application
curl http://localhost:8080/api/users
```

### Development (MySQL)
```bash
# Build and run with MySQL
docker-compose -f docker-compose.dev.yml --profile mysql up --build

# Access the application
curl http://localhost:8080/api/users

# Access Adminer (database management)
# Open http://localhost:8081 in your browser
```

### Production
```bash
# Build and run production stack
docker-compose up --build

# Access the application
curl http://localhost:8080/api/users
```

## 📋 Docker Commands

### Building Images
```bash
# Build the application image
docker build -t ecom-service .

# Build with specific tag
docker build -t ecom-service:v1.0.0 .
```

### Running Containers
```bash
# Run the application
docker run -p 8080:8080 ecom-service

# Run with environment variables
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=your-mysql-host \
  -e DB_PASSWORD=your-password \
  ecom-service

# Run in detached mode
docker run -d -p 8080:8080 --name ecom-service ecom-service
```

### Docker Compose Commands
```bash
# Start all services
docker-compose up

# Start in detached mode
docker-compose up -d

# Start specific services
docker-compose up mysql redis

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# View logs
docker-compose logs -f ecom-service

# Rebuild and start
docker-compose up --build
```

## 🔧 Configuration

### Environment Variables

#### Application Variables
```bash
SPRING_PROFILES_ACTIVE=prod
DB_HOST=mysql
DB_PORT=3306
DB_NAME=ecom_service
DB_USERNAME=ecom_user
DB_PASSWORD=ecom_password
```

#### JVM Options
```bash
JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport
```

### Ports
- **8080**: Application port
- **3306**: MySQL port (production)
- **3307**: MySQL port (development)
- **6379**: Redis port
- **8081**: Adminer port (development)

## 🏗️ Multi-Stage Build

The Dockerfile uses a multi-stage build for optimization:

### Stage 1: Builder
- Uses Maven with OpenJDK 21
- Downloads dependencies
- Compiles the application
- Creates the JAR file

### Stage 2: Runtime
- Uses slim OpenJDK 21 JRE
- Copies only the built JAR
- Runs as non-root user
- Includes health checks

## 🔒 Security Features

### Non-Root User
```dockerfile
RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser
```

### Security Headers
The application includes security headers for production.

### Environment Variables
Sensitive data is passed via environment variables, not baked into the image.

## 📊 Monitoring

### Health Checks
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
```

### Logging
```bash
# View application logs
docker-compose logs -f ecom-service

# View database logs
docker-compose logs -f mysql

# View all logs
docker-compose logs -f
```

## 🧪 Testing with Docker

### Integration Tests
```bash
# Run tests in container
docker run --rm ecom-service mvn test

# Run with test profile
docker run --rm -e SPRING_PROFILES_ACTIVE=test ecom-service
```

### Load Testing
```bash
# Using Apache Bench
docker run --rm -v $(pwd):/work httpd:alpine \
  ab -n 1000 -c 10 http://host.docker.internal:8080/api/users
```

## 🚀 Production Deployment

### Docker Swarm
```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml ecom-stack

# Scale services
docker service scale ecom-stack_ecom-service=3
```

### Kubernetes
```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/

# Check deployment
kubectl get pods -l app=ecom-service
```

## 🔧 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Check what's using the port
docker port ecom-service

# Stop conflicting containers
docker-compose down
```

#### Database Connection Issues
```bash
# Check database logs
docker-compose logs mysql

# Connect to database
docker-compose exec mysql mysql -u root -p
```

#### Memory Issues
```bash
# Check container memory usage
docker stats ecom-service

# Adjust JVM options
docker run -e JAVA_OPTS="-Xms256m -Xmx512m" ecom-service
```

### Debugging
```bash
# Run with debug mode
docker run -p 8080:8080 -p 5005:5005 \
  -e JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" \
  ecom-service

# Access container shell
docker-compose exec ecom-service sh
```

## 📈 Performance Optimization

### JVM Tuning
```bash
# Production JVM options
JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
```

### Container Optimization
- Multi-stage build reduces image size
- Non-root user for security
- Health checks for monitoring
- Proper layer caching

### Resource Limits
```yaml
services:
  ecom-service:
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '1.0'
        reservations:
          memory: 1G
          cpus: '0.5'
```

## 🔄 CI/CD Integration

### GitHub Actions Example
```yaml
name: Build and Deploy
on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Build Docker image
        run: docker build -t ecom-service .
      - name: Push to registry
        run: docker push your-registry/ecom-service
```

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [JVM Container Support](https://openjdk.java.net/jeps/351) 