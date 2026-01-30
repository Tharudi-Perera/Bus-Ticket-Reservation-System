# Bus Reservation System - Postman Quick Test URLs

## 1. HEALTH CHECK
```
GET http://localhost:8080/bus-reservation/api/test
```

---

## 2. CHECK AVAILABILITY

### A to B (Rs. 100 for 2 passengers)
```
POST http://localhost:8080/bus-reservation/api/availability
Content-Type: application/json

{
  "passengers": 2,
  "origin": "A",
  "destination": "B"
}
```

### A to C (Rs. 300 for 3 passengers)
```
POST http://localhost:8080/bus-reservation/api/availability
Content-Type: application/json

{
  "passengers": 3,
  "origin": "A",
  "destination": "C"
}
```

### A to D (Rs. 300 for 2 passengers)
```
POST http://localhost:8080/bus-reservation/api/availability
Content-Type: application/json

{
  "passengers": 2,
  "origin": "A",
  "destination": "D"
}
```

### B to D (Rs. 100 for 1 passenger)
```
POST http://localhost:8080/bus-reservation/api/availability
Content-Type: application/json

{
  "passengers": 1,
  "origin": "B",
  "destination": "D"
}
```

---

## 3. CREATE RESERVATION

### Reservation A to D (2 passengers)
```
POST http://localhost:8080/bus-reservation/api/reservation
Content-Type: application/json

{
  "passengers": 2,
  "origin": "A",
  "destination": "D",
  "price": 300.0
}
```

### Reservation B to C (3 passengers)
```
POST http://localhost:8080/bus-reservation/api/reservation
Content-Type: application/json

{
  "passengers": 3,
  "origin": "B",
  "destination": "C",
  "price": 150.0
}
```

---

## 4. ERROR TESTS

### Invalid Location (should return 400)
```
POST http://localhost:8080/bus-reservation/api/availability
Content-Type: application/json

{
  "passengers": 2,
  "origin": "X",
  "destination": "D"
}
```

### Invalid Passenger Count (should return 400)
```
POST http://localhost:8080/bus-reservation/api/availability
Content-Type: application/json

{
  "passengers": 0,
  "origin": "A",
  "destination": "D"
}
```

### Wrong Price (should return 400)
```
POST http://localhost:8080/bus-reservation/api/reservation
Content-Type: application/json

{
  "passengers": 2,
  "origin": "A",
  "destination": "D",
  "price": 100.0
}
```

---

## HOW TO USE IN POSTMAN:

1. **Health Check:**
   - New Request → GET
   - Paste: `http://localhost:8080/bus-reservation/api/test`
   - Send

2. **Availability/Reservation:**
   - New Request → POST
   - Paste URL: `http://localhost:8080/bus-reservation/api/availability` (or `/reservation`)
   - Headers tab → Add: `Content-Type: application/json`
   - Body tab → Select "raw" and "JSON"
   - Paste the JSON body
   - Send

---

## PRICING REFERENCE:
- A → B: Rs. 50 per passenger
- A → C: Rs. 100 per passenger
- A → D: Rs. 150 per passenger
- B → C: Rs. 50 per passenger
- B → D: Rs. 100 per passenger
- C → D: Rs. 50 per passenger
