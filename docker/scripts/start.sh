#!/bin/bash

# Start Docker Compose services
echo "Starting Secure App with Docker Compose..."

# Check if .env exists, if not copy from .env.example
if [ ! -f .env ]; then
    echo "Creating .env file from .env.example..."
    cp .env.example .env
    echo "Please update .env file with your configuration."
fi

# Build Tailwind CSS if output.css doesn't exist
if [ ! -f src/main/resources/static/css/output.css ]; then
    echo "Building Tailwind CSS..."
    npm install
    npm run build:css
fi

# Start services
echo "Starting Docker containers..."
docker-compose up -d --build

echo ""
echo "Services are starting..."
echo "- Application: http://localhost:8080"
echo "- MailHog UI: http://localhost:8025"
echo "- PostgreSQL: localhost:5432"
echo ""
echo "View logs with: docker-compose logs -f"
echo "Stop services with: docker-compose down"
