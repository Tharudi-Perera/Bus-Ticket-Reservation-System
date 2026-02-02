# Bus Reservation System - Client Dockerfile
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
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the JAR file from builder stage
COPY --from=builder /build/target/bus-reservation-client-jar-with-dependencies.jar /app/bus-reservation-client.jar

# Default API URL (can be overridden at runtime)
ENV API_URL=http://backend:8080/bus-reservation

# Run the client application
# Pass API_URL as argument to the Java application
CMD ["sh", "-c", "java -jar /app/bus-reservation-client.jar ${API_URL}"]
