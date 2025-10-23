#!/bin/bash

# Stop Docker Compose services
echo "Stopping Secure App Docker containers..."

docker-compose down

echo "All containers stopped."
echo ""
echo "To remove volumes (database data), run: docker-compose down -v"
