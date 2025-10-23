#!/bin/bash

# Rebuild and restart the application container
echo "Rebuilding Secure App container..."

# Stop the app service
docker-compose stop app

# Remove the app container
docker-compose rm -f app

# Rebuild and start the app
docker-compose up -d --build app

echo "Application container rebuilt and started."
echo "View logs with: docker-compose logs -f app"
