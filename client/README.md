# Bus Reservation System - Client Application

## Overview
Command-line Java client for the Bus Reservation System. Provides a user-friendly menu interface to check seat availability and make reservations via REST API.

---

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


```bash
java -cp target/bus-reservation-client.jar com.busreservation.client.BusReservationClient
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
1. Enter number of passengers
2. Select origin location (A, B, C, or D)
3. Select destination location (A, B, C, or D)

The system will display:
- Available seats
- Price per person
- Total price

### Option 2: Make Reservation

Follow the prompts to:
1. Enter number of passengers
2. Select origin location
3. Select destination location
4. Confirm the price

The system will display:
- Reservation number
- Assigned seat numbers
- Journey details
- Total price

## Configuration

### API Base URL

By default, the client connects to:
```
http://localhost:8080/bus-reservation/api
```

To change the base URL, modify the constant in `BusReservationClient.java`:
```java
private static final String BASE_URL = "http://your-server:port/bus-reservation/api";
```

## Example Session

```
Enter your choice: 1

=== Check Availability ===
Enter number of passengers: 2
Select origin:
  A - Location A
  B - Location B
  C - Location C
  D - Location D
Enter origin (A/B/C/D): A

Select destination:
  A - Location A
  B - Location B
  C - Location C
  D - Location D
Enter destination (A/B/C/D): C

Checking availability...

✓ Availability Check Successful!
Available Seats: [1A, 1B, 1C, 1D, 2A, 2B, ...]
Price per Person: Rs. 100
Total Price: Rs. 200

Press Enter to continue...
```

## Error Handling

The client handles various error scenarios:
- Invalid input (non-numeric values, invalid locations)
- API connection errors
- Server errors
- Insufficient seats
- Price mismatches

## Building for Distribution

Create a distributable JAR:

```bash
mvn clean package
```

Then share the fat JAR file:
```
client/target/bus-reservation-client-jar-with-dependencies.jar
```

## Development

### Project Structure

```
client/
├── src/
│   ├── main/
│   │   └── java/com/busreservation/client/
│   │       ├── BusReservationClient.java  # Main class
│   │       └── util/                       # Utility classes
│   └── test/
│       └── java/                           # Unit tests
└── pom.xml                                 # Maven configuration
```

### Adding New Features

1. Extend the menu system in `BusReservationClient.java`
2. Add new HTTP client methods for API calls
3. Implement input validation
4. Add error handling
5. Write unit tests

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

## Code Style

- Follow Java naming conventions
- Use 4 spaces for indentation
- Add comments for complex logic
- Keep methods small and focused
- Handle exceptions properly

## Future Enhancements

- GUI version using JavaFX or Swing
- View booking history
- Cancel reservations
- Print tickets
- Save booking details to file
- Configuration file for API URL

## License

Educational project for assignment purposes.

## Support

For issues or questions:
- Check the [API Documentation](../docs/API_DOCUMENTATION.md)
- See the [Deployment Guide](../docs/DEPLOYMENT_GUIDE.md)
- Refer to the main [README](../README.md)
