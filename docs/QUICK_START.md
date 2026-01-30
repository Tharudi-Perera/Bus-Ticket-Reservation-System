# Quick Start Guide - Testing with Postman

## Step 1: Install Apache Tomcat

### Option A: Install via apt (Recommended for Ubuntu)
```bash
sudo apt update
sudo apt install tomcat9
```

### Option B: Manual Installation
1. Download Tomcat 9 from https://tomcat.apache.org/download-90.cgi
2. Extract to `/opt/tomcat9`
3. Set `CATALINA_HOME` environment variable

## Step 2: Deploy the WAR File

### 1. Build the WAR file (if not already built)
```bash
cd /home/lakshan/Documents/jvAssignment/backend
mvn clean package
```

This creates: `target/bus-reservation.war`

### 2. Copy WAR to Tomcat webapps
```bash
# If installed via apt:
sudo cp /home/lakshan/Documents/jvAssignment/backend/target/bus-reservation.war /var/lib/tomcat9/webapps/

# If manual installation:
sudo cp /home/lakshan/Documents/jvAssignment/backend/target/bus-reservation.war /opt/tomcat9/webapps/
```

### 3. Start Tomcat
```bash
# If installed via apt:
sudo systemctl start tomcat9
sudo systemctl status tomcat9

# If manual installation:
cd /opt/tomcat9/bin
sudo ./startup.sh
```

### 4. Verify Deployment
Wait 10-20 seconds for Tomcat to deploy the WAR file, then check:
```bash
# Check Tomcat logs
sudo tail -f /var/lib/tomcat9/logs/catalina.out

# Or for manual installation:
tail -f /opt/tomcat9/logs/catalina.out
```

Look for: `Deployment of web application archive [.../bus-reservation.war] has finished`

## Step 3: Test with Browser

Open browser and navigate to:
```
http://localhost:8080/bus-reservation/api/test
```

You should see a JSON response with system information.

## Step 4: Test with Postman

### Health Check Test
1. Open Postman
2. Create new **GET** request
3. URL: `http://localhost:8080/bus-reservation/api/test`
4. Click **Send**
5. You should see: `"status": "success"`

### Check Availability Test
1. Create new **POST** request
2. URL: `http://localhost:8080/bus-reservation/api/availability`
3. Headers:
   - `Content-Type: application/json`
4. Body (raw JSON):
   ```json
   {
     "passengers": 2,
     "origin": "A",
     "destination": "D"
   }
   ```
5. Click **Send**
6. You should see available seats and price (Rs. 300)

### Create Reservation Test
1. Create new **POST** request
2. URL: `http://localhost:8080/bus-reservation/api/reservation`
3. Headers:
   - `Content-Type: application/json`
4. Body (raw JSON):
   ```json
   {
     "passengers": 2,
     "origin": "A",
     "destination": "D",
     "price": 300.0
   }
   ```
5. Click **Send**
6. You should see reservation details with seat numbers

**For detailed testing guide, see:** [POSTMAN_TESTING.md](POSTMAN_TESTING.md)

---

## Troubleshooting

### Tomcat won't start
```bash
# Check if port 8080 is already in use
sudo lsof -i :8080

# Check Tomcat status
sudo systemctl status tomcat9

# View logs
sudo tail -100 /var/lib/tomcat9/logs/catalina.out
```

### 404 Not Found
- Wait 20 seconds after copying WAR file for deployment
- Check if WAR was extracted: `ls /var/lib/tomcat9/webapps/bus-reservation/`
- Check logs for deployment errors

### Connection Refused
- Tomcat is not running: `sudo systemctl start tomcat9`
- Firewall blocking: `sudo ufw allow 8080`

### Java Version Issues
```bash
# Check Java version
java -version

# Should be Java 17 or higher
```

---

## Quick Commands Reference

```bash
# Start Tomcat
sudo systemctl start tomcat9

# Stop Tomcat
sudo systemctl stop tomcat9

# Restart Tomcat (after updating WAR)
sudo systemctl restart tomcat9

# View logs
sudo tail -f /var/lib/tomcat9/logs/catalina.out

# Rebuild and redeploy
cd /home/lakshan/Documents/jvAssignment/backend
mvn clean package
sudo systemctl stop tomcat9
sudo cp target/bus-reservation.war /var/lib/tomcat9/webapps/
sudo systemctl start tomcat9
```
