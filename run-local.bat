@echo off
echo Starting Secure App with Local Profile (H2 Database)...
echo.

REM Build Tailwind CSS if needed
if not exist "src\main\resources\static\css\output.css" (
    echo Building Tailwind CSS...
    call npm install
    call npm run build:css
)

REM Create data directory for H2 database
if not exist "data" mkdir data

echo.
echo Starting application...
echo - Profile: local
echo - Database: H2 (file-based at .\data\secureapp)
echo - H2 Console: http://localhost:8080/h2-console
echo - Application: http://localhost:8080
echo.
echo H2 Console Connection Settings:
echo   JDBC URL: jdbc:h2:file:./data/secureapp
echo   Username: sa
echo   Password: (leave empty)
echo.
echo Default Credentials:
echo   Admin: admin / admin123
echo   User: user / user123
echo.

REM Run with local profile
mvn spring-boot:run -Dspring-boot.run.profiles=local
