# Bus Ticket Reservation System - Development Plan

## Project Overview
A REST API system for bus ticket reservation between 4 locations (A→B→C→D) with 40 seats, built using core Java without frameworks.

---

## Project Structure
```
jvAssignment/
├── backend/                          # Backend REST API (WAR)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/busreservation/
│   │   │   │       ├── api/         # REST API endpoints
│   │   │   │       ├── dto/         # Data Transfer Objects (API layer)
│   │   │   │       ├── entity/      # Domain entities (business logic)
│   │   │   │       ├── service/     # Business logic
│   │   │   │       ├── repository/  # In-memory data storage
│   │   │   │       ├── util/        # Utilities (JSON parsing, etc.)
│   │   │   │       └── servlet/     # HTTP servlets
│   │   │   ├── resources/
│   │   │   └── webapp/
│   │   │       └── WEB-INF/
│   │   │           └── web.xml
│   │   └── test/
│   │       └── java/
│   │           └── com/busreservation/
│   │               └── api/         # Unit tests
│   ├── pom.xml                      # Maven build file
│   └── README.md                    # Deployment instructions
│
├── client/                          # Client application (JAR)
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── com/busreservation/client/
│   │   │           ├── BusReservationClient.java
│   │   │           └── util/
│   │   └── test/
│   │       └── java/
│   ├── pom.xml
│   └── README.md
│
├── docs/                            # Documentation
│   ├── API_DOCUMENTATION.md
│   ├── DEPLOYMENT_GUIDE.md
│   └── IMPROVEMENTS.md
│
├── .gitignore
├── README.md                        # Project overview
├── DEVELOPMENT_PLAN.md             # This file
└── Instructions.md                 # Original requirements

```

---

## Phase-by-Phase Development Plan

### Phase 1: Project Setup & Initialization
**Commit Message:** `chore: initialize project structure with git and folder organization`

**Tasks:**
- Initialize git repository
- Create `.gitignore` for Java/Maven projects
- Set up backend and client folder structure
- Create Maven POM files for both modules
- Create basic README files

**Deliverables:**
- Git repository initialized
- Proper folder structure
- Maven configuration files
- Basic documentation structure

---

### Phase 2: Backend - Domain Models
**Commit Message:** `feat(backend): implement domain models for bus reservation system`

**Tasks:**
- Create `Seat.java` - Represents a bus seat with number and status
- Create `Location.java` - Enum for locations (A, B, C, D)
- Create `Route.java` - Represents a route with origin, destination, and price
- Create `Reservation.java` - Represents a ticket reservation
- Create `Trip.java` - Represents a bus trip (A→D or D→A)
- Create `ReservationRequest.java` - DTO for reservation requests
- Create `AvailabilityResponse.java` - DTO for availability API response
- Create `ReservationResponse.java` - DTO for reservation API response

**Deliverables:**
- Complete domain model classes
- Proper encapsulation and validation
- JavaDoc documentation

---

### Phase 3: Backend - Repository Layer (In-Memory Storage)
**Commit Message:** `feat(backend): implement in-memory repository for bus data management`

**Tasks:**
- Create `BusRepository.java` - Manages seat availability
- Implement seat initialization (1A-10D, 40 seats)
- Implement seat allocation logic
- Implement seat status tracking per route segment
- Create `ReservationRepository.java` - Manages reservations
- Implement thread-safe operations using synchronization

**Deliverables:**
- Working in-memory storage
- Thread-safe operations
- Seat availability tracking by route segments

---

### Phase 4: Backend - Service Layer
**Commit Message:** `feat(backend): implement business logic for ticket reservation`

**Tasks:**
- Create `PricingService.java` - Calculate ticket prices
- Implement route pricing logic based on requirements
- Create `AvailabilityService.java` - Check seat availability
- Implement logic to find available seats for a route
- Create `ReservationService.java` - Handle ticket reservations
- Implement reservation creation and validation
- Implement seat assignment algorithm
- Add validation for passenger count, locations, and price

**Deliverables:**
- Complete business logic implementation
- Proper error handling
- Input validation

**Implementation Details:**
- **PricingService**: Singleton pattern, Rs.50 per segment pricing
- **AvailabilityService**: Integrates BusRepository and PricingService
- **ReservationService**: Complete reservation workflow with thread-safe operations


---

### Phase 5: Backend - Utility Layer
**Commit Message:** `feat(backend): add JSON utilities and helper classes`

**Tasks:**
- Create `JsonUtil.java` - JSON parsing using Jackson
- Create `HttpUtil.java` - HTTP request/response helpers
- Create `ValidationUtil.java` - Input validation utilities
- Add error response formatting

**Deliverables:**
- JSON serialization/deserialization
- HTTP utilities
- Validation utilities

**Implementation Details:**
- **JsonUtil**: Jackson-based JSON serialization/deserialization with pretty printing
- **HttpUtil**: Servlet request/response helpers with JSON support and CORS
- **ValidationUtil**: Comprehensive input validation (locations, passengers, prices, etc.)


---

### Phase 6: Backend - REST API Endpoints (Servlets)
**Commit Message:** `feat(backend): implement REST API endpoints for availability and reservation`

**Tasks:**
- Create `AvailabilityServlet.java` - GET/POST endpoint for checking availability
  - Endpoint: `/api/availability`
  - Accepts: passengers, origin, destination
  - Returns: available seats, total price
- Create `ReservationServlet.java` - POST endpoint for making reservations
  - Endpoint: `/api/reservation`
  - Accepts: passengers, origin, destination, price
  - Returns: reservation number, seat numbers, journey details, total price
- Create `BaseServlet.java` - Base class with common functionality
- Configure `web.xml` with servlet mappings
- Implement proper error handling and HTTP status codes

**Deliverables:**
- Working REST API endpoints
- Proper HTTP methods and status codes
- Error handling

---

### Phase 7: Backend - Unit Tests
**Commit Message:** `test(backend): add comprehensive unit tests for all services`

**Tasks:**
- Create `PricingServiceTest.java` - Test all route pricing scenarios
- Create `AvailabilityServiceTest.java` - Test seat availability logic
- Create `ReservationServiceTest.java` - Test reservation creation
- Create `BusRepositoryTest.java` - Test in-memory storage
- Create integration tests for complete flows
- Achieve >80% code coverage

**Deliverables:**
- Comprehensive unit tests
- Integration tests
- Test documentation

---

### Phase 8: Backend - Build & Package
**Commit Message:** `build(backend): configure Maven to generate WAR file`

**Tasks:**
- Configure `pom.xml` for WAR packaging
- Add required dependencies (Servlet API, Jackson, JUnit)
- Configure Maven compiler plugin (Java 17)
- Add Maven WAR plugin configuration
- Test WAR file generation
- Create deployment instructions

**Deliverables:**
- Functional WAR file
- Build documentation
- Deployment guide

---

### Phase 9: Client Application
**Commit Message:** `feat(client): implement command-line client for bus reservation`

**Tasks:**
- Create `BusReservationClient.java` - Main client application
- Implement menu-driven interface
- Implement HTTP client to call REST APIs
- Add functionality to check availability
- Add functionality to make reservations
- Add error handling and user-friendly messages
- Configure `pom.xml` for JAR packaging with dependencies
- Create usage documentation

**Deliverables:**
- Working client application (JAR)
- User-friendly interface
- Complete HTTP client implementation
- Client usage documentation

---

### Phase 10: Documentation
**Commit Message:** `docs: add comprehensive documentation for deployment and usage`

**Tasks:**
- Create `API_DOCUMENTATION.md` - Complete API documentation
  - Request/response examples
  - Error codes and messages
  - cURL examples
- Create `DEPLOYMENT_GUIDE.md` - Step-by-step deployment instructions
  - Prerequisites (Java, Tomcat, etc.)
  - Backend WAR deployment
  - Client JAR execution
  - Configuration steps
- Create `IMPROVEMENTS.md` - Possible improvements
  - Database integration
  - Authentication/authorization
  - Frontend web application
  - Payment integration
  - Performance optimizations
- Update main `README.md` with project overview

**Deliverables:**
- Complete API documentation
- Deployment guide
- Improvements suggestions
- Project README

---

### Phase 11: Testing & Refinement
**Commit Message:** `test: end-to-end testing and bug fixes`

**Tasks:**
- Deploy WAR to Tomcat
- Test all API endpoints manually
- Test client application
- Test edge cases and error scenarios
- Fix any discovered bugs
- Performance testing
- Code review and refactoring

**Deliverables:**
- Tested and verified system
- Bug fixes
- Performance optimizations

---

### Phase 12: Final Polish & Delivery


**Tasks:**
- Code cleanup and formatting
- Remove debug/test code
- Verify all documentation is complete
- Create release artifacts (WAR + JAR)
- Add version tags in git
- Final README review
- Create delivery package

**Deliverables:**
- Production-ready WAR file
- Production-ready JAR file
- Complete documentation
- Delivery package

---

## Git Workflow

### Branch Strategy
- `main` - Production-ready code
- `development` - Development branch
- `feature/*` - Feature branches for each phase

### Commit Convention
Follow conventional commits:
- `feat:` - New features
- `fix:` - Bug fixes
- `docs:` - Documentation only
- `test:` - Adding tests
- `chore:` - Maintenance tasks
- `refactor:` - Code refactoring
- `build:` - Build system changes

### Example Workflow
```bash
# Start new phase
git checkout -b feature/phase-2-domain-models develop

# Make changes and commit
git add .
git commit -m "feat(backend): implement domain models for bus reservation system"

# Merge to develop
git checkout develop
git merge --no-ff feature/phase-2-domain-models

# Tag important milestones
git tag -a v0.2.0 -m "Domain models completed"
```

---

## Technical Stack

### Backend
- **Language:** Java 17
- **Web Container:** Servlet API 4.0 (Tomcat 9+)
- **JSON Library:** Jackson
- **Testing:** JUnit 5, Mockito
- **Build Tool:** Maven 3.6+

### Client
- **Language:** Java 17
- **HTTP Client:** HttpURLConnection or Apache HttpClient
- **JSON Parsing:** Jackson
- **Build Tool:** Maven 3.6+

---

## Code Quality Standards

### Best Practices
1. **SOLID Principles** - Follow SOLID design principles
2. **Clean Code** - Meaningful names, small functions, clear logic
3. **Error Handling** - Proper exception handling and error messages
4. **Documentation** - JavaDoc for all public APIs
5. **Testing** - Unit tests for all business logic
6. **Thread Safety** - Synchronization for shared resources
7. **Input Validation** - Validate all user inputs
8. **Logging** - Use Java logging for debugging (optional)

### Code Style
- Follow Java naming conventions
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Organize imports properly
- Remove unused imports and variables

---

## Success Criteria

- ✅ Two working REST APIs (availability + reservation)
- ✅ WAR file that can be deployed to Tomcat
- ✅ Client JAR file with command-line interface
- ✅ In-memory data storage (no database)
- ✅ 40 seats (1A-10D) properly managed
- ✅ Correct pricing for all route combinations
- ✅ Unit tests with good coverage
- ✅ Complete documentation
- ✅ Clean, maintainable code following best practices
- ✅ Version control with meaningful commits

---

## Next Steps

1. Review and approve this development plan
2. Execute Phase 1 to set up the project structure
3. Initialize git repository
4. Begin implementation phase by phase
5. Commit after each phase completion
6. Test thoroughly after each phase

---

