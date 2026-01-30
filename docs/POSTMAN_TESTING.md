# Postman Testing Guide

## Overview
This guide shows how to test the Bus Reservation System API using Postman.

---

## Base URL
```
http://localhost:8080/bus-reservation/api
```

---

## Endpoints

### 1. Health Check / Test Endpoint
**Purpose:** Verify the backend is running and view available endpoints

**Method:** `GET`  
**URL:** `http://localhost:8080/bus-reservation/api/test`  
**Headers:** None required

**Sample Response:**
```json
{
  "status": "success",
  "message": "Bus Reservation System API is running",
  "timestamp": "2026-01-29T12:51:00",
  "version": "1.0.0",
  "system": {
    "javaVersion": "17.0.17",
    "serverInfo": "Apache Tomcat/9.0.x"
  },
  "endpoints": {
    "test": "GET /api/test - Health check endpoint",
    "availability": "POST /api/availability - Check seat availability",
    "reservation": "POST /api/reservation - Create a reservation"
  }
}
```

---

### 2. Check Seat Availability
**Purpose:** Check available seats and pricing for a route

**Method:** `POST`  
**URL:** `http://localhost:8080/bus-reservation/api/availability`  
**Headers:**
- `Content-Type: application/json`

**Request Body:**
```json
{
  "passengers": 2,
  "origin": "A",
  "destination": "C"
}
```

**Sample Response (Success):**
```json
{
  "availableSeats": ["1A", "1B", "1C", "2A", "2B", ...],
  "totalPrice": 200.0,
  "origin": "A",
  "destination": "C",
  "passengers": 2
}
```

**Sample Response (Error - Invalid Location):**
```json
{
  "error": "Bad Request",
  "message": "Invalid origin: X. Must be one of: A, B, C, D",
  "timestamp": "2026-01-29T12:51:00",
  "status": 400
}
```

---

### 3. Create Reservation
**Purpose:** Book seats for a journey

**Method:** `POST`  
**URL:** `http://localhost:8080/bus-reservation/api/reservation`  
**Headers:**
- `Content-Type: application/json`

**Request Body:**
```json
{
  "passengers": 2,
  "origin": "A",
  "destination": "C",
  "price": 200.0
}
```

**Sample Response (Success - 201 Created):**
```json
{
  "reservationId": "550e8400-e29b-41d4-a716-446655440000",
  "seatNumbers": ["1A", "1B"],
  "origin": "A",
  "destination": "C",
  "passengers": 2,
  "totalPrice": 200.0,
  "timestamp": "2026-01-29T12:51:00"
}
```

**Sample Response (Error - Seats Not Available):**
```json
{
  "error": "Conflict",
  "message": "Not enough seats available for the requested route",
  "timestamp": "2026-01-29T12:51:00",
  "status": 409
}
```

**Sample Response (Error - Invalid Price):**
```json
{
  "error": "Bad Request",
  "message": "Price mismatch: expected 200.0 but received 150.0",
  "timestamp": "2026-01-29T12:51:00",
  "status": 400
}
```

---

## Testing Workflow

### Step 1: Verify Backend is Running
1. Open Postman
2. Create a new `GET` request
3. Enter URL: `http://localhost:8080/bus-reservation/api/test`
4. Click **Send**
5. You should see status `200 OK` with system information

### Step 2: Check Seat Availability
1. Create a new `POST` request
2. Enter URL: `http://localhost:8080/bus-reservation/api/availability`
3. Go to **Headers** tab, add:
   - Key: `Content-Type`
   - Value: `application/json`
4. Go to **Body** tab, select **raw** and **JSON**
5. Enter request body:
   ```json
   {
     "passengers": 2,
     "origin": "A",
     "destination": "D"
   }
   ```
6. Click **Send**
7. You should see available seats and total price

### Step 3: Create a Reservation
1. Create a new `POST` request
2. Enter URL: `http://localhost:8080/bus-reservation/api/reservation`
3. Add `Content-Type: application/json` header
4. Enter request body with the price from availability check:
   ```json
   {
     "passengers": 2,
     "origin": "A",
     "destination": "D",
     "price": 300.0
   }
   ```
5. Click **Send**
6. You should see status `201 Created` with reservation details

### Step 4: Test Error Scenarios

**Invalid Location:**
```json
{
  "passengers": 2,
  "origin": "X",
  "destination": "D"
}
```
Expected: `400 Bad Request`

**Invalid Passenger Count:**
```json
{
  "passengers": 0,
  "origin": "A",
  "destination": "D"
}
```
Expected: `400 Bad Request`

**Wrong Price:**
```json
{
  "passengers": 2,
  "origin": "A",
  "destination": "D",
  "price": 100.0
}
```
Expected: `400 Bad Request` (price mismatch)

---

## Pricing Information

The bus operates on route: **A → B → C → D**

**Pricing:** Rs. 50 per segment

| Route | Segments | Price |
|-------|----------|-------|
| A → B | 1 | Rs. 50 |
| A → C | 2 | Rs. 100 |
| A → D | 3 | Rs. 150 |
| B → C | 1 | Rs. 50 |
| B → D | 2 | Rs. 100 |
| C → D | 1 | Rs. 50 |

**Example:** For 2 passengers from A → D:
- Base price: Rs. 150 (3 segments)
- Total: Rs. 150 × 2 = Rs. 300

---

## Common Issues

### 1. Connection Refused
- **Cause:** Backend server is not running
- **Solution:** Deploy the WAR file to Tomcat and start the server

### 2. 404 Not Found
- **Cause:** Incorrect URL or context path
- **Solution:** Verify the context path is `/bus-reservation`

### 3. 500 Internal Server Error
- **Cause:** Server-side error (check Tomcat logs)
- **Solution:** Check `catalina.out` or console logs

### 4. CORS Error (from browser)
- **Cause:** Cross-origin request blocked
- **Solution:** API includes CORS headers, ensure OPTIONS requests are allowed

---

## Postman Collection

You can import this collection to test all endpoints:

```json
{
  "info": {
    "name": "Bus Reservation System",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "header": [],
        "url": "http://localhost:8080/bus-reservation/api/test"
      }
    },
    {
      "name": "Check Availability",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"passengers\": 2,\n  \"origin\": \"A\",\n  \"destination\": \"D\"\n}"
        },
        "url": "http://localhost:8080/bus-reservation/api/availability"
      }
    },
    {
      "name": "Create Reservation",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"passengers\": 2,\n  \"origin\": \"A\",\n  \"destination\": \"D\",\n  \"price\": 300.0\n}"
        },
        "url": "http://localhost:8080/bus-reservation/api/reservation"
      }
    }
  ]
}
```

Save this as `Bus_Reservation_System.postman_collection.json` and import into Postman.
