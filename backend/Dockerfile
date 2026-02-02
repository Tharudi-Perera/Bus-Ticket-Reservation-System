# Bus Reservation System - Backend Dockerfile
# Multi-stage build for optimized image size

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml first for better layer caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create runtime image
FROM tomcat:9.0-jre17-temurin-jammy

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from builder stage
COPY --from=builder /build/target/bus-reservation.war /usr/local/tomcat/webapps/bus-reservation.war

# Expose Tomcat port
EXPOSE 8080

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/bus-reservation/api/test || exit 1

# Start Tomcat
CMD ["catalina.sh", "run"]
