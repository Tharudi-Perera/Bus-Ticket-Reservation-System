# Running the Bus Reservation Client

This guide shows all the different ways to run the client application.

---

## Prerequisites

1. **Backend API must be running** on Tomcat
2. **Client JAR built**: Run `mvn clean package` if not already built

---

## Method 1: Standard Mode (No Network Logs)

### Using Fat JAR (Recommended)
```bash
cd client
java -jar target/bus-reservation-client-jar-with-dependencies.jar
```

### With Custom API URL
```bash
java -jar target/bus-reservation-client-jar-with-dependencies.jar http://your-server:8080/bus-reservation
```

### Using Maven
```bash
cd client
mvn exec:java -Dexec.mainClass="com.busreservation.client.BusReservationClient"
```

---

## Method 2: Debug Mode (With Network Call Logging)

Shows all HTTP requests and responses with full JSON payloads.

### Using Fat JAR
```bash
cd client
java -Ddebug=true -jar target/bus-reservation-client-jar-with-dependencies.jar
```

### With Custom API URL
```bash
java -Ddebug=true -jar target/bus-reservation-client-jar-with-dependencies.jar http://your-server:8080/bus-reservation
```

### Using Maven
```bash
cd client
mvn exec:java -Dexec.mainClass="com.busreservation.client.BusReservationClient" -Ddebug=true
```

---

## Debug Mode Output Example

When running with `-Ddebug=true`, you'll see detailed network information:

```
======================================================================
🌐 HTTP REQUEST
======================================================================
Method: POST
URL: http://localhost:8080/bus-reservation/api/availability

Request Body:
{
  "passengers" : 2,
  "origin" : "A",
  "destination" : "C"
}
======================================================================

⏳ Checking availability...

======================================================================
🌐 HTTP RESPONSE
======================================================================
Status Code: 200

Response Body:
{
  "availableSeats" : [ "1A", "1B", "1C", "1D", "2A", ... ],
  "totalPrice" : 200.0,
  "pricePerSeat" : 100.0,
  "passengers" : 2,
  "origin" : "A",
  "destination" : "C"
}
======================================================================
```

---

## Quick Commands

### Standard Run
```bash
# From project root
cd client && java -jar target/bus-reservation-client-jar-with-dependencies.jar

# Or absolute path
java -jar /home/lakshan/Documents/jvAssignment/client/target/bus-reservation-client-jar-with-dependencies.jar
```

### Debug Run
```bash
# From project root
cd client && java -Ddebug=true -jar target/bus-reservation-client-jar-with-dependencies.jar

# Or absolute path
java -Ddebug=true -jar /home/lakshan/Documents/jvAssignment/client/target/bus-reservation-client-jar-with-dependencies.jar
```

### Rebuild and Run
```bash
# Rebuild JAR and run
cd client && mvn clean package -DskipTests && java -jar target/bus-reservation-client-jar-with-dependencies.jar

# Rebuild and run with debug
cd client && mvn clean package -DskipTests && java -Ddebug=true -jar target/bus-reservation-client-jar-with-dependencies.jar
```

---

## System Properties Reference

| Property | Values | Description |
|----------|--------|-------------|
| `-Ddebug` | `true` / `false` | Enable/disable network call logging (default: `false`) |
| First argument | URL string | Custom backend API URL (default: `http://localhost:8080/bus-reservation`) |

---

## Usage Examples

### 1. Local Development (Standard)
```bash
cd client
java -jar target/bus-reservation-client-jar-with-dependencies.jar
```

### 2. Local Development (Debug Network Calls)
```bash
cd client
java -Ddebug=true -jar target/bus-reservation-client-jar-with-dependencies.jar
```

### 3. Remote Server
```bash
cd client
java -jar target/bus-reservation-client-jar-with-dependencies.jar http://remote-server:8080/bus-reservation
```

### 4. Remote Server with Debug
```bash
cd client
java -Ddebug=true -jar target/bus-reservation-client-jar-with-dependencies.jar http://remote-server:8080/bus-reservation
```

---

## Troubleshooting

### Backend Not Running
**Error:** Service unavailable / Connection refused

**Solution:**
```bash
# Check if backend is running
curl http://localhost:8080/bus-reservation/api/test

# Start Tomcat if needed
sudo systemctl start tomcat9

# Check Tomcat status
sudo systemctl status tomcat9
```

### JAR File Not Found
**Error:** Could not find or load main class

**Solution:**
```bash
# Rebuild the JAR
cd client
mvn clean package
```

### Debug Mode Not Working
**Issue:** Network logs not appearing

**Check:**
- Ensure `-Ddebug=true` appears **before** `-jar` in command
- ✅ Correct: `java -Ddebug=true -jar ...`
- ❌ Wrong: `java -jar ... -Ddebug=true`

---

## Performance Notes

- **Debug Mode:** Adds minimal overhead (~5-10ms per request) for console output
- **Standard Mode:** No performance impact, production-ready
- **Timeouts:** 5-second connection timeout, 5-second read timeout

---

## When to Use Each Mode

### Standard Mode
- ✅ Normal user operations
- ✅ Production usage
- ✅ When you don't need to see network traffic
- ✅ Cleaner console output

### Debug Mode
- ✅ Development and testing
- ✅ Troubleshooting API issues
- ✅ Verifying request/response formats
- ✅ Understanding what's being sent to backend
- ✅ Debugging timeout or error scenarios

---

## Additional Notes

1. **Exit Client:** Enter `3` in the main menu or press `Ctrl+C`
2. **Multiple Instances:** You can run multiple client instances simultaneously
3. **Log Files:** Currently logs to console only (no file logging)
4. **Backend URL:** Must include `/bus-reservation` context path
5. **JSON Format:** All network communication uses UTF-8 encoded JSON

---

## See Also

- [Client README](README.md) - Full client documentation
- [Backend README](../backend/README.md) - Backend deployment guide
- [API Documentation](../docs/API_DOCUMENTATION.md) - API specifications
- [Quick Start Guide](../docs/QUICK_START.md) - Overall system setup
