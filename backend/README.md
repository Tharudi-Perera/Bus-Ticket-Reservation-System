# Bus Reservation Backend
REST API backend for the bus ticket reservation system.

## 📋 Table of Contents
- [Overview](#overview)
- [Implemented Features](#implemented-features)
- [Technology Stack](#technology-stack)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [System Architecture](#system-architecture)
- [Concurrency & Thread Safety](#concurrency--thread-safety)
- [Testing](#testing)

## Overview
This is a Java-based REST API built using Servlets (without frameworks like Spring or JAX-RS) for managing bus ticket reservations.

## Implemented Features
- Check seat availability and pricing
- Reserve tickets with automatic seat assignment
- Thread-safe concurrent reservations with ReentrantLock
- Fair queueing (FIFO) for reservation requests
- Timeout protection to prevent system hangs
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


## Quick Start
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

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/busreservation/
│   │   │   ├── api/          # API layer (servlets)
│   │   │   ├── dto/          # Data Transfer Objects (API contracts)
│   │   │   ├── entity/       # Domain entities (business models)
│   │   │   ├── service/      # Business logic layer (with concurrency control)
│   │   │   ├── repository/   # Data access layer (thread-safe)
│   │   │   ├── util/         # Utility classes
│   │   │   └── servlet/      # HTTP servlet endpoints
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── web.xml   # Servlet configuration
│   └── test/
│       └── java/             # Unit and integration tests
│
│── Dockerfile
│
│── README.md
│
└── pom.xml                   # Maven configuration

```

## System Architecture
This project follows **clean architecture** and **SOLID principles**:

### Layer Separation
- **DTO Layer** (`dto/`): Data Transfer Objects for API input/output
  - Clean API contracts independent of internal implementation
  - Annotated with Jackson for JSON serialization
  - Examples: `AvailabilityRequestDTO`, `ReservationResponseDTO`

- **Entity Layer** (`entity/`): Domain models
  - Core business objects (Location, Route, Seat, Reservation)
  - Encapsulate business rules and validations
  - Independent of external concerns (API)

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


## Concurrency & Thread Safety
### Overview
The system handles multiple concurrent reservation requests safely using a multi-layered approach:

### 1. **ReentrantLock with Fair Queueing**
**Features:**
- **Serialized Processing**: Only one reservation processed at a time
- **Fair FIFO Ordering**: First request gets processed first (prevents starvation)
- **Timeout Protection**: 3-second maximum wait time
- **Graceful Degradation**: Returns "system busy" error instead of hanging

### 2. **Synchronized Seat Allocation**

**Protects Against:**
- ❌ Race conditions
- ❌ Double-booking
- ❌ Data corruption
- ❌ Overselling


## Testing
Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=BusReservationSystemTest
```

Generate test coverage report:
```bash
mvn jacoco:report
```
