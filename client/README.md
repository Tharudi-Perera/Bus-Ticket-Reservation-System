# Bus Reservation System - Client Application
Client for the bus ticket reservation system.

## 📋 Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Usage](#usage)
- [Error Handling](#error-handling)
- [Troubleshooting](#troubleshooting)
- [Testing](#testing)

## Overview
Command-line Java client for the Bus Reservation System. Provides a user-friendly menu interface to check seat availability and make reservations via REST API.

---

## Prerequisites
- **Java 17** or higher
- **Maven 3.6+**
- **Backend API** running (default: `http://localhost:8080/bus-reservation`)

---

## Quick Start

### 1. Build JAR with Dependencies

```bash
cd client
mvn clean package
```

This creates two JAR files in `target/`:
- `bus-reservation-client.jar` - Regular JAR
- `bus-reservation-client-jar-with-dependencies.jar` - Fat JAR (includes all dependencies)

---

### 2. Running the Client
### Option 1: Using the Fat JAR (Recommended)

```bash
java -jar target/bus-reservation-client-jar-with-dependencies.jar
```

### Option 2: With Custom API URL

```bash
java -jar target/bus-reservation-client-jar-with-dependencies.jar http://your-server:8080/bus-reservation
```

```bash
java -cp target/bus-reservation-client.jar com.busreservation.client.BusReservationClient
```

## Project Structure

```
client/
├── src/
│   ├── main/
│   │   └── java/com/busreservation/client/
│   │       ├── BusReservationClient.java   # Main class
│   │   │   ├── dto/                        # Data Transfer Objects
│   │       └── util/                       # Utility classes
│   └── test/
│       └── java/                           # Unit tests
│
│── Dockerfile
│
│── README.md
│
└── pom.xml                   # Maven configuration
```


## Usage

When you run the client, you'll see a menu:

```
===========================================
   Bus Ticket Reservation System
===========================================

1. Check Availability
2. Make Reservation
3. Exit

Enter your choice:
```

### Option 1: Check Availability

Follow the prompts to:
1. Enter the trip date
2. Enter number of passengers
3. Select origin location (A, B, C, or D)
4. Select destination location (A, B, C, or D)

The system will display:
- Available seats
- Price per person
- Total price

### Option 2: Make Reservation

Follow the prompts to:
1. Enter the trip date
2. Enter number of passengers
3. Select origin location
4. Select destination location
5. Confirm the price

The system will display:
- Reservation number
- Assigned seat numbers
- Journey details
- Total price


## Error Handling

The client handles various error scenarios:
- Invalid input (non-numeric values, invalid locations)
- API connection errors
- Server errors
- Insufficient seats
- Price mismatches


## Troubleshooting

### Connection Refused
- Ensure the backend API is running
- Check the API URL and port
- Verify firewall settings

### Invalid Input
- Enter numeric values for passenger count
- Use only A, B, C, or D for locations
- Follow the on-screen prompts

### Build Failures
- Verify Java version: `java -version`
- Verify Maven version: `mvn -version`
- Clean and rebuild: `mvn clean install`


## Testing

Run unit tests:
```bash
mvn test
```