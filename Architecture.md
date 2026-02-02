# Bus Ticket Reservation System - Architecture

## Table of Contents
1. [System Overview](#system-overview)
2. [Architecture Diagram](#architecture-diagram)
3. [Design Patterns](#design-patterns)
4. [SOLID Principles](#solid-principles)
5. [Data Flow](#data-flow)
6. [Thread Safety](#thread-safety)
7. [Testing Strategy](#testing-strategy)
8. [Conclusion](#conclusion)

---

## System Overview

The Bus Ticket Reservation System is a REST API-based application built with **core Java** (no frameworks) following clean architecture principles. It manages bus ticket reservations for a route with 4 locations (A → B → C → D) and 40 seats.

### Key Features
- Real-time seat availability checking
- Thread-safe concurrent reservations with ReentrantLock**
- Fair queueing (FIFO) for reservation requests**
- Timeout protection to prevent system hangs**
- Concurrent reservation handling
- Dynamic pricing based on route segments
- Thread-safe operations
- In-memory data persistence

### Technology Stack
- **Backend**: Java 17, Servlet API 4.0, Jackson (JSON)
- **Server**: Apache Tomcat 9+
- **Client**: Java CLI application
- **Build Tool**: Maven 3.6+
- **Testing**: JUnit 5, Mockito

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         CLIENT TIER                         │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  BusReservationClient.java (CLI Application)           │ │
│  │  - Interactive menu interface                          │ │
│  │  - User input handling                                 │ │
│  │  - Response display                                    │ │
│  └──────────────────┬─────────────────────────────────────┘ │
│                     │                                       │
│  ┌──────────────────▼─────────────────────────────────────┐ │
│  │  HttpClient.java (HTTP Utility)                        │ │
│  │  - POST/GET request handling                           │ │
│  │  - JSON serialization/deserialization                  │ │
│  │  - Error handling & timeouts                           │ │
│  └─────────────────┬──────────────────────────────────────┘ │
└────────────────────┼────────────────────────────────────────┘
                     │ HTTP/JSON
                     │
┌────────────────────▼──────────────────────────────────────┐
│                      API LAYER (Servlets)                 │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  AvailabilityServlet                                 │ │
│  │  POST /api/availability                              │ │
│  │  - Accepts: passengers, origin, destination, tripDate│ │
│  │  - Returns: available seats, pricing                 │ │
│  └──────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  ReservationServlet                                  │ │
│  │  POST /api/reservation                               │ │
│  │  - Accepts: passengers, origin, destination, price   │ │
│  │  - Returns: reservation details, seat assignments    │ │
│  │  - If system busy (lock timeout)                     │ │ 
│  └──────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  TestServlet (HealthCheck)                           │ │
│  │  GET /api/test                                       │ │
│  │  - System status check                               │ │
│  └─────────────────┬────────────────────────────────────┘ │
└────────────────────┼──────────────────────────────────────┘
                     │
┌────────────────────▼──────────────────────────────────────┐
│                    SERVICE LAYER                          │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  AvailabilityService (Singleton)                     │ │
│  │  - Check seat availability for routes                │ │
│  │  - Calculate total pricing                           │ │
│  │  - Validate passenger count                          │ │
│  │  - Read-only operations (no lock required)           │ │
│  └──────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  ReservationService (Singleton)                      │ │
│  │  - Create reservations (lock-protected)              │ │
│  │  - Assign seats automatically                        │ │
│  │  - Validate pricing                                  │ │
│  │  - Generate reservation numbers                      │ │
│  │  - ReentrantLock (Fair FIFO)                         │ │
│  └──────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  PricingService (Singleton)                          │ │
│  │  - Calculate route prices (Rs.50/segment)            │ │
│  │  - Support all route combinations                    │ │
│  └─────────────────┬────────────────────────────────────┘ │
└────────────────────┼──────────────────────────────────────┘
                     │
┌────────────────────▼──────────────────────────────────────┐
│                  REPOSITORY LAYER                         │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  BusRepository (Singleton)                           │ │
│  │  - Manage 40 seats (1A-10D)                          │ │
│  │  - Track seat status per route segment               │ │
│  │  - Thread-safe operations (synchronized)             │ │
│  │  - In-memory storage (ConcurrentHashMap)             │ │
│  └──────────────────────────────────────────────────────┘ │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  ReservationRepository (Singleton)                   │ │
│  │  - Store reservations in-memory                      │ │
│  │  - Generate unique reservation IDs                   │ │
│  │  - Thread-safe operations                            │ │
│  └──────────────────────────────────────────────────────┘ │
└───────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                     ENTITY/DTO LAYER                        │
│  Domain Models:  Seat, Location, Route, Reservation         │
│  DTOs: AvailabilityRequest/Response, ReservationRequest/    │
│        Response, ErrorResponseDTO                           │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      UTILITY LAYER                          │
│  JsonUtil, HttpUtil, ValidationUtil                         │
└─────────────────────────────────────────────────────────────┘
```

## Design Patterns

### 1. **Singleton Pattern**
**Used in**: All service classes and repositories

**Why**: 
- Ensures single instance managing shared resources
- Thread-safe access to in-memory data
- Consistent state across application

---

### 2. **Repository Pattern**
**Used in**: BusRepository, ReservationRepository

**Why**:
- Abstracts data access logic
- Separates business logic from data persistence
- Easy to swap in-memory storage with database

**Benefits**:
- Can replace with JPA/JDBC without changing service layer
- Centralized data access logic
- Testable with mock repositories

---

### 3. **Data Transfer Object (DTO) Pattern**
**Used in**: All API request/response objects

**Why**:
- Decouple API layer from domain entities
- Control exactly what data is exposed
- Version API independently from domain model

---

### 4. **Strategy Pattern (Implicit)**
**Used in**: PricingService

**Why**:
- Encapsulates pricing algorithm
- Easy to change pricing rules without affecting other components
- Could extend to support multiple pricing strategies

---

## SOLID Principles

### 1. **Single Responsibility Principle (SRP)**
Each class has one reason to change:
- **AvailabilityService**: Only handles availability checks
- **ReservationService**: Only handles reservations
- **PricingService**: Only handles pricing calculations
- **BusRepository**: Only manages seat data
- **ReservationRepository**: Only manages reservation data

---

### 2. **Open/Closed Principle (OCP)**
Open for extension, closed for modification:
- Service interfaces can have multiple implementations
- PricingService can be extended with new pricing strategies
- Repository implementations can be swapped (in-memory → database)

---

### 3. **Liskov Substitution Principle (LSP)**
Interfaces define contracts that implementations must honor:
- Repository interfaces can be replaced with different implementations
- Services depend on interfaces, not concrete classes

---

### 4. **Interface Segregation Principle (ISP)**
Small, focused interfaces:
- Each repository has specific methods for its concern
- Services don't depend on methods they don't use
- DTOs are specific to each use case

---

### 5. **Dependency Inversion Principle (DIP)**
Depend on abstractions, not concretions:
- Services depend on repository interfaces
- High-level modules (services) don't depend on low-level modules (repositories)
- Both depend on abstractions

---

## Data Flow

### 1. **Check Availability Flow**

```
Client
  │
  │ POST /api/availability
  │ {passengers: 2, origin: "A", destination: "C", tripDate: "2026-03-15"}
  │
  ▼
AvailabilityServlet
  │
  │ 1. Parse & validate JSON
  │ 2. Create AvailabilityRequest DTO
  │
  ▼
AvailabilityService
  │
  │ 1. Validate passenger count (1-40)
  │ 2. Call BusRepository.getAvailableSeats()
  │ 3. Call PricingService.calculatePrice()
  │ 4. Build AvailabilityResponse
  │
  ▼
BusRepository (In-Memory)
  │
  │ 1. Check seat status for route segments (A-B, B-C)
  │ 2. Find seats available on ALL segments
  │ 3. Return List<String> of seat numbers
  │
  ▼
AvailabilityServlet
  │
  │ Return 200 OK with JSON response
  │ {availableSeats: ["1A","1B",...], pricePerPerson: 100, totalPrice: 200}
  │
  ▼
Client (Display results)
```

---

### 2. **Create Reservation Flow**

```
Client
  │
  │ POST /api/reservation
  │ {passengers: 2, origin: "A", destination: "C", price: 200, tripDate: "2026-03-15"}
  │
  ▼
ReservationServlet
  │
  │ 1. Parse & validate JSON
  │ 2. Create ReservationRequest DTO
  │
  ▼
ReservationService (synchronized)
  │
  │ ┌─────────────────────────────────────┐
  │ │ TRY TO ACQUIRE REENTRANTLOCK (FAIR) │
  │ │ Timeout: 3 seconds                  │
  │ └─────────────────────────────────────┘
  │
  ├─ IF LOCK ACQUIRED (within 3s):
  │   │
  │   │ 1. Validate price with PricingService
  │   │ 2. Check seat availability for specific date
  │   │ 3. If available, assign seats
  │   │ 4. Create Reservation entity
  │   │ 5. Save to ReservationRepository
  │   │ 6. Update BusRepository seat status for that date
  │   │ 7. Build ReservationResponse
  │   │
  │   └─ FINALLY: Release lock
  │
  └─ IF LOCK TIMEOUT (> 3s):
      │
      └─ Throw IllegalStateException("System busy...")
  │
  │
  ▼
BusRepository (synchronized)
  │
  │ 1. Get available seats for route
  │ 2. Mark seats as RESERVED for all segments
  │ 3. Update in-memory seat map
  │
  ▼
ReservationRepository
  │
  │ 1. Generate unique reservation number
  │ 2. Save reservation to in-memory list
  │ 3. Return saved reservation
  │
  ▼
ReservationServlet
  │
  ├─ IF SUCCESS:
  │   └─ Return 201 Created
  │      {reservationId: "RES-...", seatNumbers: ["1A","1B"], ...}
  │
  └─ IF LOCK TIMEOUT:
      └─ Return 503 Service Unavailable
         {error: "System is currently busy..."}
  │
  │
  ▼
Client (Display confirmation or retry prompt)
```

---

## Thread Safety

### Concurrency Challenges
The system handles concurrent seat reservations where multiple users might try to book the same seats simultaneously.

### Solutions Implemented

 - Synchronized Methods
 - Thread-Safe Collections
 - Optimistic Locking Pattern


---

## Testing Strategy

### Security Considerations
- Input validation on all endpoints
- JSON parsing with error handling
- HTTP status codes for different error types

### Testing Strategy

### Unit Tests
- Service layer logic (pricing, validation)
- Repository operations
- Singleton pattern verification

### Integration Tests
- End-to-end reservation flow
- All location combinations
- Error handling scenarios

### Concurrent Tests
- Race condition scenarios
- Thread-safety verification
- No overselling validation

---

## Conclusion

This architecture provides a solid foundation for a bus reservation system with:
- Clean separation of concerns
- Thread-safe concurrent operations
- Fair queuing to prevent starvation
- SOLID principles adherence
- Testable components
- Scalable design patterns

The system successfully handles the core requirements while maintaining code quality and extensibility for future enhancements.