# Multi-stage build for e-commerce service
# Stage 1: Build the application
FROM maven:3.9.6-openjdk-21 AS builder

# Set working directory
WORKDIR /app

# Copy the parent pom.xml first for better layer caching
COPY pom.xml ./

# Copy all module pom.xml files
COPY domain/pom.xml ./domain/
COPY infrastructure/pom.xml ./infrastructure/
COPY service/pom.xml ./service/
COPY security/pom.xml ./security/
COPY web/pom.xml ./web/

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY domain/src ./domain/src
COPY infrastructure/src ./infrastructure/src
COPY service/src ./service/src
COPY security/src ./security/src
COPY web/src ./web/src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime image
FROM openjdk:21-jre-slim

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy the built JAR from builder stage
COPY --from=builder /app/web/target/*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Set JVM options for production
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 