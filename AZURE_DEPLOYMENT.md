# Azure VM Deployment Guide

## Overview
This guide explains how to deploy the Bus Reservation Backend to Azure VM using Docker.

**Target VM:** nuwa@52.151.196.202  
**Backend Port:** 8082  
**Technology:** Docker + Tomcat 9.0 + JDK 17

---

## Prerequisites

### On Your Local Machine:
- Docker installed and running
- SSH access to Azure VM configured
- SSH key or password for nuwa@52.151.196.202

### On Azure VM (nuwa@52.151.196.202):
- Docker installed and running
- Port 8082 open in Azure Network Security Group (NSG)
- User 'nuwa' has Docker permissions (added to docker group)

---

## Quick Deployment

### Option 1: Automated Script (Recommended)

```bash
# Make the script executable
chmod +x deploy-to-azure.sh

# Run the deployment
./deploy-to-azure.sh
```

The script will:
1. Build the Docker image locally
2. Save it as a tar file
3. Transfer to Azure VM via SCP
4. Load and run the container on Azure VM
5. Clean up temporary files

### Option 2: Manual Deployment

```bash
# Step 1: Build the image locally
cd backend
docker build -t bus-reservation-backend:latest .
cd ..

# Step 2: Save the image
docker save -o bus-reservation-backend.tar bus-reservation-backend:latest

# Step 3: Copy to Azure VM
scp bus-reservation-backend.tar nuwa@52.151.196.202:~/

# Step 4: SSH to Azure VM and deploy
ssh nuwa@52.151.196.202

# On Azure VM:
docker load -i ~/bus-reservation-backend.tar
docker stop bus-reservation-backend 2>/dev/null || true
docker rm bus-reservation-backend 2>/dev/null || true
docker run -d \
    --name bus-reservation-backend \
    --restart unless-stopped \
    -p 8082:8080 \
    bus-reservation-backend:latest

# Verify
docker ps
curl http://localhost:8082/bus-reservation/api/test

# Cleanup
rm ~/bus-reservation-backend.tar
exit
```

---

## Verification

After deployment, test the backend:

```bash
# From your local machine
curl http://52.151.196.202:8082/bus-reservation/api/test

# Expected response:
# {"status":"success","message":"Bus Reservation System API is running","timestamp":"..."}
```

---

## Azure VM Setup (First Time Only)

If Docker is not installed on the Azure VM, run these commands:

```bash
# SSH to Azure VM
ssh nuwa@52.151.196.202

# Install Docker
sudo apt-get update
sudo apt-get install -y docker.io

# Start and enable Docker
sudo systemctl start docker
sudo systemctl enable docker

# Add user to docker group (to run without sudo)
sudo usermod -aG docker nuwa

# Log out and log back in for group changes to take effect
exit
ssh nuwa@52.151.196.202

# Verify Docker installation
docker --version
docker ps
```

### Open Port in Azure NSG

1. Go to Azure Portal
2. Navigate to your VM → Networking → Network Security Group
3. Add inbound rule:
   - **Port:** 8082
   - **Protocol:** TCP
   - **Action:** Allow
   - **Priority:** 1000
   - **Name:** Allow-Backend-8082

---

## Container Management

### View Logs
```bash
ssh nuwa@52.151.196.202 'docker logs -f bus-reservation-backend'
```

### Check Status
```bash
ssh nuwa@52.151.196.202 'docker ps'
```

### Restart Container
```bash
ssh nuwa@52.151.196.202 'docker restart bus-reservation-backend'
```

### Stop Container
```bash
ssh nuwa@52.151.196.202 'docker stop bus-reservation-backend'
```

### Remove Container
```bash
ssh nuwa@52.151.196.202 'docker rm -f bus-reservation-backend'
```

### Access Container Shell
```bash
ssh nuwa@52.151.196.202 'docker exec -it bus-reservation-backend bash'
```

---

## API Endpoints

Once deployed, access these endpoints:

- **Health Check:** `http://52.151.196.202:8082/bus-reservation/api/test`
- **Check Availability:** `http://52.151.196.202:8082/bus-reservation/api/availability`
- **Make Reservation:** `http://52.151.196.202:8082/bus-reservation/api/reservation`

### Example API Calls

```bash
# Health check
curl http://52.151.196.202:8082/bus-reservation/api/test

# Check availability
curl -X POST http://52.151.196.202:8082/bus-reservation/api/availability \
  -H "Content-Type: application/json" \
  -d '{
    "origin": "New York",
    "destination": "Boston",
    "date": "2026-02-20",
    "passengers": 2
  }'

# Make reservation
curl -X POST http://52.151.196.202:8082/bus-reservation/api/reservation \
  -H "Content-Type: application/json" \
  -d '{
    "origin": "New York",
    "destination": "Boston",
    "date": "2026-02-20",
    "passengers": 2,
    "customerName": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890"
  }'
```

---

## Troubleshooting

### Container won't start
```bash
# Check logs
ssh nuwa@52.151.196.202 'docker logs bus-reservation-backend'

# Check if port is in use
ssh nuwa@52.151.196.202 'sudo netstat -tlnp | grep 8082'
```

### Can't access from internet
- Verify Azure NSG allows port 8082
- Check VM firewall: `sudo ufw status`
- Verify container is running: `docker ps`

### Permission denied errors
```bash
# Add user to docker group
ssh nuwa@52.151.196.202 'sudo usermod -aG docker nuwa'
# Then log out and back in
```

### Out of disk space
```bash
# Clean up old images
ssh nuwa@52.151.196.202 'docker system prune -a'
```

---

## Redeployment (Updates)

To deploy a new version:

```bash
# Simply run the deployment script again
./deploy-to-azure.sh
```

The script automatically:
- Stops the old container
- Removes it
- Deploys the new version
- Starts the new container

---

## Security Recommendations

1. **Use SSH keys** instead of passwords
2. **Enable firewall** on Azure VM:
   ```bash
   sudo ufw allow 22    # SSH
   sudo ufw allow 8082  # Backend
   sudo ufw enable
   ```
3. **Configure HTTPS** with reverse proxy (nginx) if needed
4. **Use environment variables** for sensitive data
5. **Regular updates**: Keep Docker and system packages updated

---

## Monitoring

### Basic Health Monitoring Script

Create on Azure VM (`~/monitor-backend.sh`):

```bash
#!/bin/bash
while true; do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/bus-reservation/api/test)
    if [ "$STATUS" != "200" ]; then
        echo "$(date): Backend unhealthy (HTTP $STATUS), restarting..."
        docker restart bus-reservation-backend
    fi
    sleep 60
done
```

Run with: `nohup ~/monitor-backend.sh > ~/monitor.log 2>&1 &`

---

## Backup and Restore

### Backup container data (if needed)
```bash
ssh nuwa@52.151.196.202 'docker export bus-reservation-backend > backup.tar'
```

### Restore from backup
```bash
docker import backup.tar bus-reservation-backend:backup
```

---

## Support

For issues or questions, check:
- Container logs: `docker logs bus-reservation-backend`
- Backend README: [backend/README.md](backend/README.md)
- Architecture: [Architecture.md](Architecture.md)
