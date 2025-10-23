#!/bin/bash

# View Docker Compose logs
SERVICE=${1:-}

if [ -z "$SERVICE" ]; then
    echo "Following logs for all services..."
    echo "Press Ctrl+C to stop"
    docker-compose logs -f
else
    echo "Following logs for $SERVICE..."
    echo "Press Ctrl+C to stop"
    docker-compose logs -f "$SERVICE"
fi
