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
│   │   │   ├── api/          # API layer (servlets)
│   │   │   ├── dto/          # Data Transfer Objects (API contracts)
│   │   │   ├── entity/       # Domain entities (business models)
│   │   │   ├── service/      # Business logic layer
│   │   │   ├── repository/   # Data access layer
│   │   │   ├── util/         # Utility classes
│   │   │   └── servlet/      # HTTP servlet endpoints
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── web.xml   # Servlet configuration
│   └── test/
│       └── java/             # Unit and integration tests
└── pom.xml                   # Maven configuration
```

## Architecture & SOLID Principles

This project follows **clean architecture** and **SOLID principles**:

### Layer Separation
- **DTO Layer** (`dto/`): Data Transfer Objects for API input/output
  - Clean API contracts independent of internal implementation
  - Annotated with Jackson for JSON serialization
  - Examples: `AvailabilityRequestDTO`, `ReservationResponseDTO`

- **Entity Layer** (`entity/`): Domain models with business logic
  - Core business objects (Location, Route, Seat, Reservation)
  - Encapsulate business rules and validations
  - Independent of external concerns (API, database)

- **Service Layer** (`service/`): Business logic orchestration
  - Coordinate between repositories and entities
  - Implement use cases and workflows
  - Transaction boundaries

- **Repository Layer** (`repository/`): Data access abstraction
  - Manage data persistence (in-memory for this project)
  - Thread-safe operations
  - Can be swapped for database implementation

### SOLID Principles Applied

1. **Single Responsibility**: Each class has one reason to change
   - DTOs only handle data transfer
   - Entities only contain business logic
   - Services orchestrate workflows
   - Repositories manage data access

2. **Open/Closed**: Open for extension, closed for modification
   - Service interfaces allow multiple implementations
   - Strategy pattern for pricing logic

3. **Liskov Substitution**: Interfaces define contracts
   - Repository interfaces can be swapped
   - Service implementations are interchangeable

4. **Interface Segregation**: Focused interfaces
   - Small, specific interfaces for each concern
   - Clients depend only on what they need

5. **Dependency Inversion**: Depend on abstractions
   - High-level modules don't depend on low-level modules
   - Both depend on interfaces


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

1. **Create DTOs** in `dto/` package for API contracts
2. **Create/update entities** in `entity/` package for business models
3. **Implement business logic** in `service/` package
4. **Add data access** in `repository/` package
5. **Create servlet endpoints** in `servlet/` or `api/` package
6. **Write unit tests** in `test/` directory

### DTO vs Entity Pattern

**DTOs** (Data Transfer Objects):
- Used for API input/output only
- No business logic
- JSON annotations for serialization
- May flatten or aggregate data for API needs
- Example: `AvailabilityRequestDTO` receives JSON from client

**Entities** (Domain Models):
- Contain business logic and rules
- Represent core domain concepts
- Independent of API structure
- Encapsulate behavior
- Example: `Route` validates pricing and calculates segments


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
