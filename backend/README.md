# Bus Reservation Backend

REST API backend for the bus ticket reservation system.

## Overview

This is a Java-based REST API built using Servlets (without frameworks like Spring or JAX-RS) for managing bus ticket reservations.

## Features

- Check seat availability and pricing
- Reserve tickets with automatic seat assignment
- In-memory data storage
- Thread-safe operations
- JSON request/response format
- Comprehensive error handling

## Technology Stack

- Java 17
- Servlet API 4.0
- Jackson for JSON processing
- JUnit 5 for testing
- Mockito for mocking
- Maven for build management

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/busreservation/
│   │   │   ├── api/          # API layer
│   │   │   ├── model/        # Domain models
│   │   │   ├── service/      # Business logic
│   │   │   ├── repository/   # Data storage
│   │   │   ├── util/         # Utilities
│   │   │   └── servlet/      # HTTP servlets
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── web.xml   # Servlet configuration
│   └── test/
│       └── java/             # Unit tests
└── pom.xml                   # Maven configuration
```

## Building the Project

```bash
# Clean and build
mvn clean package

# Run tests
mvn test

# Skip tests during build
mvn clean package -DskipTests
```

The WAR file will be generated at: `target/bus-reservation.war`

## Running the Application

### Prerequisites
- Apache Tomcat 9+ installed
- Java 17 installed

### Deployment Steps

1. Build the WAR file:
```bash
mvn clean package
```

2. Deploy to Tomcat:
```bash
cp target/bus-reservation.war $CATALINA_HOME/webapps/
```

3. Start Tomcat:
```bash
$CATALINA_HOME/bin/startup.sh
```

4. Access the API at:
```
http://localhost:8080/bus-reservation/api/
```

For detailed deployment instructions, see [Deployment Guide](../docs/DEPLOYMENT_GUIDE.md).

## API Endpoints

### Check Availability
- **URL**: `/api/availability`
- **Method**: POST
- **Request**:
```json
{
  "passengers": 2,
  "origin": "A",
  "destination": "C"
}
```

### Reserve Tickets
- **URL**: `/api/reservation`
- **Method**: POST
- **Request**:
```json
{
  "passengers": 2,
  "origin": "A",
  "destination": "C",
  "price": 200
}
```

For complete API documentation, see [API Documentation](../docs/API_DOCUMENTATION.md).

## Testing

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=PricingServiceTest
```

Generate test coverage report:
```bash
mvn jacoco:report
```

## Development

### Adding New Features

1. Create domain models in `model/` package
2. Implement business logic in `service/` package
3. Add data access in `repository/` package
4. Create servlet endpoints in `servlet/` package
5. Write unit tests in `test/` directory

### Code Style

- Follow Java naming conventions
- Use 4 spaces for indentation
- Add JavaDoc for public APIs
- Keep methods small and focused
- Use meaningful variable names

## Configuration

The application configuration is in:
- `src/main/webapp/WEB-INF/web.xml` - Servlet mappings
- `pom.xml` - Dependencies and build configuration

## Troubleshooting

### Port Already in Use
Change Tomcat port in `$CATALINA_HOME/conf/server.xml`

### Out of Memory
Increase heap size: `export CATALINA_OPTS="-Xms512m -Xmx1024m"`

### Build Failures
- Verify Java version: `java -version`
- Verify Maven version: `mvn -version`
- Clean and rebuild: `mvn clean install`

## License

Educational project for assignment purposes.

## Contributors

Java Developer Assignment Project
