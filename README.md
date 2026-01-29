# Bus-Ticket-Reservation-System
Complete bus reservation system: thread-safe REST API backend (Java Servlets, Tomcat) + interactive CLI frontend (Java HttpClient). Implements ReentrantReadWriteLock for race-free booking, clean layered architecture, and RESTful design. No framework dependencies - pure Java mastery.



# Bus Reservation System

> A complete REST API-based bus ticket reservation system built with core Java, Servlets, and Maven. This project includes a backend WAR file and a CLI client JAR file.

---

## 🚀 Quick Reference

### Build, Test & Deploy

#### **Windows**
```cmd
REM Navigate to project root
cd C:\path\to\project

REM Build backend + run tests
cd backend
mvn clean package

REM Build client
cd ..\client
mvn clean package

REM Deploy to Tomcat (Run as Administrator)
copy backend\target\bus-reservation.war "C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\"

REM Start Tomcat (if using service)
net start Tomcat9

REM Or start Tomcat (if manual install)
"C:\Program Files\Apache Software Foundation\Tomcat 9.0\bin\startup.bat"

REM Run client
cd client
java -jar target\bus-reservation-client-jar-with-dependencies.jar
```

#### **Ubuntu/Linux**
```bash
# Navigate to project root
cd /path/to/project

# Build backend + run tests
cd backend
mvn clean package

# Build client
cd ../client
mvn clean package

# Deploy to Tomcat
sudo cp backend/target/bus-reservation.war /var/lib/tomcat9/webapps/

# Start/Restart Tomcat
sudo systemctl restart tomcat9

# Run client
cd client
java -jar target/bus-reservation-client-jar-with-dependencies.jar
```

#### **macOS**
```bash
# Navigate to project root
cd /path/to/project

# Build backend + run tests
cd backend
mvn clean package

# Build client
cd ../client
mvn clean package

# Deploy to Tomcat (Homebrew installation)
cp backend/target/bus-reservation.war /usr/local/Cellar/tomcat/*/libexec/webapps/

# Or if using manual installation
cp backend/target/bus-reservation.war $CATALINA_HOME/webapps/

# Start/Restart Tomcat (Homebrew)
brew services restart tomcat

# Or manual start
$CATALINA_HOME/bin/startup.sh

# Run client
cd client
java -jar target/bus-reservation-client-jar-with-dependencies.jar
```

### Quick Test
```bash
# Test backend API (works on all platforms)
curl http://localhost:8080/bus-reservation/api/test

# Expected response: "Backend is running!"
```

---

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


## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Backend API** running (default: `http://localhost:8080/bus-reservation`)

---

## Building the Client

### 1. Build JAR with Dependencies

```bash
cd client
mvn clean package
```

This creates two JAR files in `target/`:
- `bus-reservation-client.jar` - Regular JAR
- `bus-reservation-client-jar-with-dependencies.jar` - Fat JAR (includes all dependencies)

---

## Running the Client

### Option 1: Using the Fat JAR (Recommended)

```bash
java -jar target/bus-reservation-client-jar-with-dependencies.jar
```

### Option 2: With Custom API URL

```bash
java -jar target/bus-reservation-client-jar-with-dependencies.jar http://your-server:8080/bus-reservation
```

### Option 3: Using Maven

```bash
java -cp target/bus-reservation-client.jar com.busreservation.client.BusReservationClient

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Documentation](#documentation)
- [Technology Stack](#technology-stack)
- [Development](#development)
- [Testing](#testing)
- [License](#license)

---

## 🎯 Overview

The Bus Reservation System is a full-stack application that manages ticket reservations for a bus service operating between four locations (A → B → C → D) with 40 seats (1A-10D). The system consists of:

- **Backend**: RESTful API built with Java Servlets, packaged as WAR
- **Client**: Interactive command-line application, packaged as executable JAR
- **Storage**: Thread-safe in-memory data structures (no database required)

### Bus Route

```
A ──→ B ──→ C ──→ D
  50    50    50   (Price in Rs. per segment)
```

### Key Features

✅ **Real-time Availability Check** - Query available seats for any route  
✅ **Instant Reservations** - Book tickets with automatic seat assignment  
✅ **Segment-based Pricing** - Rs. 50 per segment (A→C = Rs. 100)  
✅ **Thread-safe Operations** - Handles concurrent users safely  
✅ **RESTful API** - Clean HTTP endpoints with JSON  
✅ **Interactive Client** - User-friendly command-line interface  
✅ **Debug Mode** - Network call logging for troubleshooting  

---

## 🚀 Features

### Backend API
- **Check Availability** (`POST /api/availability`)
  - Returns available seats list
  - Calculates total price and per-seat price
  - Validates passenger count and locations
  
- **Make Reservation** (`POST /api/reservation`)
  - Creates reservation with UUID
  - Assigns specific seat numbers
  - Returns booking confirmation with timestamp
  
- **Health Check** (`GET /api/test`)
  - System status and version info
  - Available endpoints list

### Client Application
- **Menu-driven Interface** - Easy navigation with numbered options
- **Availability Check** - View seats and pricing before booking
- **Reservation Booking** - Confirm and complete bookings
- **Error Handling** - User-friendly error messages
- **Debug Mode** - Optional network call logging with `-Ddebug=true`

### Technical Features
- **Concurrency Control** - Synchronized reservation operations
- **Input Validation** - Comprehensive validation on both client and server
- **CORS Support** - Cross-origin requests enabled
- **Error Responses** - Proper HTTP status codes (200, 201, 400, 405, 409, 500)
- **Timeout Handling** - 5-second connection and read timeouts

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENT APPLICATION                       │
│  ┌───────────────┐  ┌──────────────┐  ┌──────────────────┐ │
│  │ Menu UI       │  │ HTTP Client  │  │ DTOs             │ │
│  └───────────────┘  └──────────────┘  └──────────────────┘ │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTP/JSON
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                      BACKEND API (WAR)                       │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              API Layer (Servlets)                      │  │
│  │  AvailabilityServlet │ ReservationServlet │ TestServlet  │
│  └────────────────────────┬──────────────────────────────┘  │
│                           │                                  │
│  ┌────────────────────────┴──────────────────────────────┐  │
│  │              Service Layer                             │  │
│  │  AvailabilityService │ ReservationService │ PricingService │
│  └────────────────────────┬──────────────────────────────┘  │
│                           │                                  │
│  ┌────────────────────────┴──────────────────────────────┐  │
│  │            Repository Layer                            │  │
│  │       BusRepository │ ReservationRepository            │  │
│  └────────────────────────┬──────────────────────────────┘  │
│                           │                                  │
│  ┌────────────────────────┴──────────────────────────────┐  │
│  │         In-Memory Storage (ConcurrentHashMap)          │  │
│  └────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
project/
├── backend/                          # Backend REST API (WAR)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/busreservation/
│   │   │   │   ├── api/              # REST endpoints (Servlets)
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── entity/           # Domain models
│   │   │   │   ├── service/          # Business logic
│   │   │   │   ├── repository/       # Data access
│   │   │   │   └── util/             # Utilities
│   │   │   └── webapp/WEB-INF/
│   │   │       └── web.xml           # Servlet configuration
│   │   └── test/java/                # Unit tests
│   ├── pom.xml
│   └── README.md
│
├── client/                           # Client application (JAR)
│   ├── src/main/java/com/busreservation/client/
│   │   ├── BusReservationClient.java # Main application
│   │   ├── dto/                      # Request/Response DTOs
│   │   └── util/                     # HTTP client
│   ├── pom.xml
│   ├── README.md
│   └── RUNNING_THE_CLIENT.md         # Detailed running guide
│
├── docs/                             # Documentation
│   ├── API_DOCUMENTATION.md          # API specifications
│   ├── DEPLOYMENT_GUIDE.md           # Deployment instructions
│   ├── IMPROVEMENTS.md               # Enhancement suggestions
│   ├── POSTMAN_TESTING.md            # Postman testing guide
│   └── QUICK_START.md                # Quick setup guide
│
├── Bus_Reservation_System.postman_collection.json
├── POSTMAN_QUICK_TEST.md
├── DEVELOPMENT_PLAN.md               # Development roadmap
├── Instructions.md                   # Original requirements
└── README.md                         # This file
```

---

## ⚡ Quick Start

### Prerequisites

- **Java 17** or higher ([Download](https://openjdk.org/))
- **Maven 3.6+** ([Download](https://maven.apache.org/))
- **Apache Tomcat 9+** ([Download](https://tomcat.apache.org/))

### 1. Clone & Build

**Linux/Mac:**
```bash
# Clone the repository
cd /path/to/project

# Build backend
cd backend
mvn clean package

# Build client
cd ../client
mvn clean package
```

**Windows:**
```cmd
:: Navigate to project directory
cd C:\path\to\project

:: Build backend
cd backend
mvn clean package

:: Build client
cd ..\client
mvn clean package
```

### 2. Deploy Backend

**Linux/Mac:**
```bash
# Copy WAR to Tomcat
sudo cp backend/target/bus-reservation.war /var/lib/tomcat9/webapps/

# Start Tomcat
sudo systemctl start tomcat9

# Verify deployment
curl http://localhost:8080/bus-reservation/api/test
```

**Windows:**
```cmd
:: Copy WAR to Tomcat webapps folder
copy backend\target\bus-reservation.war "C:\Program Files\Apache Software Foundation\Tomcat 9.0\webapps\"

:: Start Tomcat (run as Administrator)
"C:\Program Files\Apache Software Foundation\Tomcat 9.0\bin\startup.bat"

:: Verify deployment (use browser or PowerShell)
start http://localhost:8080/bus-reservation/api/test
```

### 3. Run Client

**Linux/Mac:**
```bash
cd client
java -jar target/bus-reservation-client-jar-with-dependencies.jar
```

**Windows:**
```cmd
cd client
java -jar target\bus-reservation-client-jar-with-dependencies.jar
```

### 4. Test with Postman (Optional)

Import `Bus_Reservation_System.postman_collection.json` into Postman and run the test requests.

---

## 📖 Documentation

### Core Guides

| Document | Description |
|----------|-------------|
| **[PROJECT_SETUP.md](PROJECT_SETUP.md)** | **Complete setup guide for Windows & Ubuntu** |
| **[DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** | **Backend deployment & client execution (Windows & Ubuntu)** |
| **[TESTING_GUIDE.md](TESTING_GUIDE.md)** | **Testing procedures - Backend, Client, API tests** |
| **[ARCHITECTURE.md](ARCHITECTURE.md)** | **System architecture & design documentation** |

### API & Reference

| Document | Description |
|----------|-------------|
| [API Documentation](docs/API_DOCUMENTATION.md) | Complete REST API reference with examples |
| [Postman Testing](docs/POSTMAN_TESTING.md) | API testing with Postman collection |
| [Improvements](docs/IMPROVEMENTS.md) | Suggested future enhancements |

### Quick Access

- **Setup Environment**: [PROJECT_SETUP.md](PROJECT_SETUP.md) → Install Java, Maven, Tomcat
- **Deploy & Run**: [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) → Deploy WAR, run JAR
- **Test System**: [TESTING_GUIDE.md](TESTING_GUIDE.md) → Run tests, verify APIs
- **Understand Design**: [ARCHITECTURE.md](ARCHITECTURE.md) → System design & patterns

---

## 🛠️ Technology Stack

### Backend
- **Java 17** - Programming language
- **Servlet API 4.0** - Web framework
- **Jackson 2.16.1** - JSON processing
- **Maven 3.6+** - Build automation
- **JUnit 5** - Testing framework
- **Mockito 5** - Mocking framework
- **Apache Tomcat 9** - Servlet container

### Client
- **Java 17** - Programming language
- **HttpURLConnection** - HTTP client
- **Jackson 2.16.1** - JSON processing
- **Maven 3.6+** - Build automation

### Architectural Patterns
- **Layered Architecture** - Separation of concerns (API → Service → Repository)
- **Singleton Pattern** - Services and repositories
- **DTO Pattern** - Data transfer objects
- **Repository Pattern** - Data access abstraction

---

## 💻 Development

### Building from Source

```bash
# Build entire project
mvn clean package

# Build only backend
cd backend && mvn clean package

# Build only client
cd client && mvn clean package

# Skip tests
mvn clean package -DskipTests
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=BusReservationSystemTest

# Run with coverage
mvn test jacoco:report
```

### Running Client in Debug Mode

```bash
# See network calls (requests/responses)
java -Ddebug=true -jar target/bus-reservation-client-jar-with-dependencies.jar
```

---

## 🧪 Testing

### Backend Testing
- **12 Integration Tests** covering:
  - Singleton pattern verification
  - Pricing calculations for all routes
  - Seat availability checks
  - Reservation creation and management
  - Location validation
  - Repository operations

### API Testing
- **Postman Collection** with test cases for:
  - Health check endpoint
  - Availability checks (all routes)
  - Successful reservations
  - Error scenarios (400, 409 errors)
  
### Client Testing
- Manual testing via interactive interface
- Debug mode for network call verification

---

## 📋 API Endpoints Summary

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/test` | GET | System health check |
| `/api/availability` | POST | Check seat availability and pricing |
| `/api/reservation` | POST | Create a new reservation |

**Base URL**: `http://localhost:8080/bus-reservation`

---

## 🎯 Pricing Table

| Route | Distance | Price per Seat |
|-------|----------|----------------|
| A → B | 1 segment | Rs. 50 |
| A → C | 2 segments | Rs. 100 |
| A → D | 3 segments | Rs. 150 |
| B → C | 1 segment | Rs. 50 |
| B → D | 2 segments | Rs. 100 |
| C → D | 1 segment | Rs. 50 |

*Pricing is segment-based: Rs. 50 per segment traveled*

---

## 📝 Bus Information

- **Seats**: 40 seats (1A, 1B, 1C, 1D ... 10A, 10B, 10C, 10D)
- **Route**: A → B → C → D (and return D → A)
- **Pricing**:
  - A→B: Rs. 50
  - A→C: Rs. 100
  - A→D: Rs. 150
  - B→C: Rs. 50
  - B→D: Rs. 100
  - C→D: Rs. 50

---

## 🔐 Thread Safety & Concurrency

The system handles concurrent reservations safely:
- `ConcurrentHashMap` for seat storage
- `synchronized` methods for reservation operations
- Atomic seat allocation
- Race condition handling (409 Conflict when seats unavailable)

⚠️ **Known Race Condition**: There's a window between availability check and reservation where seats can be taken. See [DEVELOPMENT_PLAN.md - Phase 11](DEVELOPMENT_PLAN.md#phase-11-testing--refinement) for details and proposed solutions.

---

## 🚦 Getting Help

- **Backend Issues**: Check logs in `/var/log/tomcat9/`
- **Client Connection**: Verify backend with `curl http://localhost:8080/bus-reservation/api/test`
- **Postman Testing**: Import collection and follow [POSTMAN_TESTING.md](docs/POSTMAN_TESTING.md)
- **Debug Network**: Run client with `-Ddebug=true` flag

---

## 🤝 Contributing

This project follows:
- **SOLID Principles** - Clean, maintainable code
- **Layered Architecture** - Clear separation of concerns
- **Conventional Commits** - Standardized commit messages
- **Documentation-first** - Every feature is documented

See [DEVELOPMENT_PLAN.md](DEVELOPMENT_PLAN.md) for the complete development workflow.

---

## � Release Information

**Current Version**: v1.0.0  
**Release Date**: January 29, 2026  
**Status**: Production Ready ✅

### Release Package

A complete release package is available in [`release/v1.0.0/`](release/v1.0.0/):
- **bus-reservation.war** - Production-ready backend (2.1 MB)
- **bus-reservation-client.jar** - Executable client (2.2 MB)
- **Complete documentation** - All guides and references
- **Postman collection** - API testing suite
- **RELEASE_NOTES.md** - Detailed release information

### Version History

- **v1.0.0** (2026-01-29) - Initial production release
  - Core reservation system with 2 REST APIs
  - Thread-safe concurrent operations (15 tests passing)
  - Complete documentation (1,800+ lines)
  - Production-ready artifacts

---
