#!/bin/bash

echo "Starting Secure App with Local Profile (H2 Database)..."
echo ""

# Build Tailwind CSS if needed
if [ ! -f src/main/resources/static/css/output.css ]; then
    echo "Building Tailwind CSS..."
    npm install
    npm run build:css
fi

# Create data directory for H2 database
mkdir -p data

echo ""
echo "Starting application..."
echo "- Profile: local"
echo "- Database: H2 (file-based at ./data/secureapp)"
echo "- H2 Console: http://localhost:8080/h2-console"
echo "- Application: http://localhost:8080"
echo ""
echo "H2 Console Connection Settings:"
echo "  JDBC URL: jdbc:h2:file:./data/secureapp"
echo "  Username: sa"
echo "  Password: (leave empty)"
echo ""
echo "Default Credentials:"
echo "  Admin: admin / admin123"
echo "  User: user / user123"
echo ""

# Run with local profile
mvn spring-boot:run -Dspring-boot.run.profiles=local
