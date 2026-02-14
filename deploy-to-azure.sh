#!/bin/bash

# Deployment script for Bus Reservation Backend to Azure VM
# Target: nuwa@52.151.196.202

set -e  # Exit on error

echo "=========================================="
echo "Bus Reservation Backend - Azure Deployment"
echo "=========================================="

# Configuration
AZURE_USER="nuwa"
AZURE_HOST="52.151.196.202"
IMAGE_NAME="bus-reservation-backend"
IMAGE_TAG="latest"
IMAGE_FILE="bus-reservation-backend.tar"
CONTAINER_NAME="bus-reservation-backend"
HOST_PORT="8082"
CONTAINER_PORT="8080"

# Step 1: Build the Docker image
echo ""
echo "[1/5] Building Docker image..."
cd backend
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
cd ..

# Step 2: Save the Docker image to a tar file
echo ""
echo "[2/5] Saving Docker image to tar file..."
docker save -o ${IMAGE_FILE} ${IMAGE_NAME}:${IMAGE_TAG}
echo "Image saved as ${IMAGE_FILE} ($(du -h ${IMAGE_FILE} | cut -f1))"

# Step 3: Transfer the image to Azure VM
echo ""
echo "[3/5] Transferring image to Azure VM..."
echo "Uploading to ${AZURE_USER}@${AZURE_HOST}..."
scp ${IMAGE_FILE} ${AZURE_USER}@${AZURE_HOST}:~/

# Step 4: Load and run the image on Azure VM
echo ""
echo "[4/5] Loading and running image on Azure VM..."
ssh ${AZURE_USER}@${AZURE_HOST} << 'ENDSSH'
    echo "Loading Docker image..."
    docker load -i ~/bus-reservation-backend.tar
    
    echo "Stopping and removing old container if exists..."
    docker stop bus-reservation-backend 2>/dev/null || true
    docker rm bus-reservation-backend 2>/dev/null || true
    
    echo "Starting new container..."
    docker run -d \
        --name bus-reservation-backend \
        --restart unless-stopped \
        -p 8082:8080 \
        bus-reservation-backend:latest
    
    echo "Cleaning up tar file..."
    rm ~/bus-reservation-backend.tar
    
    echo ""
    echo "Container status:"
    docker ps -a | grep bus-reservation-backend
    
    echo ""
    echo "Waiting for application to start..."
    sleep 10
    
    echo "Checking application health..."
    curl -f http://localhost:8082/bus-reservation/api/test || echo "Health check failed - application may still be starting"
ENDSSH

# Step 5: Cleanup local tar file
echo ""
echo "[5/5] Cleaning up local tar file..."
rm ${IMAGE_FILE}

echo ""
echo "=========================================="
echo "Deployment Complete!"
echo "=========================================="
echo ""
echo "Backend is now running at: http://52.151.196.202:8082/bus-reservation"
echo ""
echo "Useful commands:"
echo "  - View logs:    ssh ${AZURE_USER}@${AZURE_HOST} 'docker logs -f ${CONTAINER_NAME}'"
echo "  - Stop:         ssh ${AZURE_USER}@${AZURE_HOST} 'docker stop ${CONTAINER_NAME}'"
echo "  - Restart:      ssh ${AZURE_USER}@${AZURE_HOST} 'docker restart ${CONTAINER_NAME}'"
echo "  - Remove:       ssh ${AZURE_USER}@${AZURE_HOST} 'docker rm -f ${CONTAINER_NAME}'"
echo ""
echo "Test the API:"
echo "  curl http://52.151.196.202:8082/bus-reservation/api/test"
echo ""
