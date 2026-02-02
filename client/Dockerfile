# Multi-stage build for Bus Reservation Client
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package

# Production stage
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the JAR with dependencies from build stage
COPY --from=build /app/target/bus-reservation-client-jar-with-dependencies.jar /app/client.jar

# Set the API URL as environment variable (can be overridden)
ENV API_URL=http://backend:8080/bus-reservation

# Run the client application with API_URL from environment
ENTRYPOINT ["sh", "-c", "java -jar /app/client.jar $API_URL"]
