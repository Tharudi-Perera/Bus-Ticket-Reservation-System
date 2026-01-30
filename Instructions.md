"complete and send us a war file where as the client can be a jar file with related documentation on how to deploy and execute."



# Java Developer Assignment: Bus Ticket Reservation REST API

## Scenario
You need to develop a simple REST API to reserve tickets for a bus. The bus runs between four locations: **A, B, C, and D**, making stops at each.

---

## Requirements

### 1. Bus Information
- The bus has **40 seats**, arranged in rows of 4 seats each (e.g., 1A, 1B, …, 10D).
- The bus makes a **one-way trip from A to D** and then **returns from D to A** once per day.

### 2. Pricing
The ticket prices for different routes are:

| Route | Price (Rs.) |
|-------|------------|
| A → B | 50         |
| A → C | 100        |
| A → D | 150        |
| B → C | 50         |
| B → D | 100        |
| C → D | 50         |

> Same prices are applicable for return journeys as well.

### 3. Tasks
Implement **two REST APIs** using Java.  
**Preferred:** Without using frameworks like Spring or JAX-RS, but you can use a JSON library for handling JSON.

#### API 1: Check Availability & Price
- **Accepts:** Number of passengers, origin, and destination
- **Returns:**
  - Available seats
  - Total price

#### API 2: Reserve Tickets
- **Accepts:** Passenger count, origin, destination, and price confirmation
- **Returns:**
  - Reservation or ticket number
  - Assigned seat numbers
  - Journey details (departure & arrival locations)
  - Total price

### 4. Additional Tasks
- Provide **unit tests** for the APIs.
- Document how to **run the application**.
- Suggest **possible improvements**.

---

## Restrictions
- Using **core Java** is preferred over frameworks (e.g., Spring, JAX-RS) but not mandatory.
- Store data **in memory** (no database required).
- Package the service in a **WAR file** for deployment.
