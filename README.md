# Secure App - Spring Boot Application

A secure, high-performance Spring Boot web application with comprehensive user management, role-based access control (RBAC), and modern UI using Tailwind CSS.

## Features

### Security Features
- **Spring Security** integration with BCrypt password encryption
- **Role-Based Access Control (RBAC)** with Users, Roles, and Permissions
- **Authentication Methods:**
  - Traditional username/password login
  - Magic link (passwordless) login
  - Remember me functionality
- **Password Management:**
  - Password reset via email
  - Secure token-based reset flow
- **Account Security:**
  - Failed login attempt tracking
  - Account locking after multiple failed attempts
  - Session management

### User Management
- Complete CRUD operations for Users, Roles, and Permissions
- User search and pagination
- Multi-role assignment
- Fine-grained permission control
- Audit trails (created/updated timestamps)

### Modern UI
- **Tailwind CSS** with Metronic-inspired design
- Responsive design (mobile, tablet, desktop)
- Clean, professional interface
- Flash messages for user feedback
- Form validation with inline errors

## Technology Stack

- **Java 21** (Note: Java 25 doesn't exist yet; using latest LTS)
- **Spring Boot 3.4.0**
- **Spring Security 6**
- **Spring Data JPA**
- **PostgreSQL** database
- **Thymeleaf** template engine
- **Tailwind CSS 3.4.1**
- **Flyway** for database migrations
- **Lombok** for reducing boilerplate
- **Maven** for dependency management

## Prerequisites

1. **Java Development Kit (JDK) 21 or higher**
   ```bash
   java -version
   ```

2. **PostgreSQL 12 or higher**
   ```bash
   psql --version
   ```

3. **Node.js and npm** (for Tailwind CSS)
   ```bash
   node --version
   npm --version
   ```

4. **Maven 3.6+** (or use the included Maven wrapper)
   ```bash
   mvn --version
   ```

### Docker Alternative

If you prefer to use Docker (recommended for quick setup):
- **Docker** 20.10+
- **Docker Compose** 2.0+

See [DOCKER.md](DOCKER.md) for Docker installation and setup instructions.

## Quick Start with Docker (Recommended)

The fastest way to get started:

```bash
# 1. Clone and navigate to the project
git clone <repository-url>
cd secure-app

# 2. Copy environment file
cp .env.example .env

# 3. Build Tailwind CSS
npm install && npm run build:css

# 4. Start all services with Docker
./docker/scripts/start.sh
```

Access the application at http://localhost:8080

**Default Credentials:**
- Admin: `admin` / `admin123`
- User: `user` / `user123`

**Included Services:**
- Application: http://localhost:8080
- MailHog (Email UI): http://localhost:8025
- PostgreSQL: localhost:5432

For detailed Docker instructions, see [DOCKER.md](DOCKER.md)

## Quick Start with H2 Database (No PostgreSQL Required)

For local development without Docker or PostgreSQL:

```bash
# 1. Clone and navigate to the project
git clone <repository-url>
cd secure-app

# 2. Build Tailwind CSS
npm install && npm run build:css

# 3. Run with local profile (H2 database)
./run-local.sh   # Linux/Mac
run-local.bat    # Windows
```

Or manually:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**What you get:**
- H2 file-based database (data persists in `./data/secureapp.mv.db`)
- H2 Console at http://localhost:8080/h2-console
- Application at http://localhost:8080
- MailHog not included (emails logged to console)

**H2 Console Connection:**
- **JDBC URL:** `jdbc:h2:file:./data/secureapp`
- **Username:** `sa`
- **Password:** (leave empty)

**Default Credentials:**
- Admin: `admin` / `admin123`
- User: `user` / `user123`

## Installation & Setup (Without Docker)

### 1. Clone the Repository
```bash
git clone <repository-url>
cd secure-app
```

### 2. Database Setup

Create a PostgreSQL database:
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE secureapp_dev;

# Exit psql
\q
```

### 3. Configure Database Connection

Edit `src/main/resources/application-dev.yml` if needed:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/secureapp_dev
    username: postgres
    password: postgres
```

### 4. Install Tailwind CSS Dependencies
```bash
npm install
```

### 5. Build Tailwind CSS
```bash
npm run build:css
```

For development with auto-rebuild:
```bash
npm run watch:css
```

### 6. Build the Application
```bash
./mvnw clean install
```

## Running the Application

### Development Mode
```bash
./mvnw spring-boot:run
```

The application will start on **http://localhost:8080**

### Production Mode
```bash
# Set active profile to production
export SPRING_PROFILES_ACTIVE=prod

# Run with production settings
./mvnw spring-boot:run
```

## Default Credentials

### Admin Account
- **Username:** `admin`
- **Password:** `admin123`
- **Roles:** ADMIN (full access)

### Regular User Account
- **Username:** `user`
- **Password:** `user123`
- **Roles:** USER (basic access)

**⚠️ IMPORTANT:** Change these passwords immediately in production!

## Application Structure

```
src/
├── main/
│   ├── java/com/example/secureapp/
│   │   ├── config/           # Configuration classes
│   │   │   └── SecurityConfig.java
│   │   ├── controller/       # Web controllers
│   │   │   ├── AuthController.java
│   │   │   ├── UserController.java
│   │   │   ├── RoleController.java
│   │   │   └── PermissionController.java
│   │   ├── model/           # Entity classes
│   │   │   ├── User.java
│   │   │   ├── Role.java
│   │   │   └── Permission.java
│   │   ├── repository/      # Data access layer
│   │   ├── service/         # Business logic
│   │   ├── security/        # Security handlers
│   │   └── dto/            # Data transfer objects
│   └── resources/
│       ├── templates/       # Thymeleaf templates
│       │   ├── auth/       # Authentication pages
│       │   ├── users/      # User CRUD pages
│       │   ├── roles/      # Role CRUD pages
│       │   └── permissions/ # Permission CRUD pages
│       ├── static/
│       │   └── css/        # CSS files
│       ├── db/migration/   # Flyway migrations
│       └── application.yml  # Configuration
```

## Available Endpoints

### Public Pages
- `/auth/login` - Login page
- `/auth/register` - Registration page
- `/auth/forgot-password` - Password reset request
- `/auth/reset-password` - Password reset form
- `/auth/request-magic-link` - Magic link request
- `/auth/magic-link` - Magic link login

### Protected Pages (Authentication Required)
- `/dashboard` - Main dashboard
- `/users` - User management (ADMIN, USER_MANAGER)
- `/roles` - Role management (ADMIN only)
- `/permissions` - Permission management (ADMIN only)

## Configuration

### Email Configuration

For password reset and magic link features, configure SMTP in `application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### Security Configuration

Update JWT secret and token expiration in `application.yml`:

```yaml
app:
  security:
    jwt-secret: YourVeryLongRandomSecretKey
    token-expiration: 86400000 # 24 hours
    magic-link-expiration: 3600000 # 1 hour
    password-reset-expiration: 3600000 # 1 hour
```

## Database Migrations

Flyway automatically runs migrations on startup. Migration files are located in:
```
src/main/resources/db/migration/
```

- `V1__initial_schema.sql` - Creates tables and indexes
- `V2__seed_data.sql` - Inserts default data

## Development

### Hot Reload

The application uses Spring DevTools for automatic restart on code changes.

### Tailwind CSS Development

Run Tailwind in watch mode while developing:
```bash
npm run watch:css
```

This will automatically rebuild CSS when template files change.

### Adding New Permissions

1. Add permission to database:
```sql
INSERT INTO permissions (name, description, active)
VALUES ('NEW_PERMISSION', 'Description', TRUE);
```

2. Assign to roles as needed via UI or SQL

3. Use in controllers:
```java
@PreAuthorize("hasAuthority('NEW_PERMISSION')")
public String myMethod() { ... }
```

## Testing

Run tests:
```bash
./mvnw test
```

## Production Deployment

### 1. Build Production JAR
```bash
./mvnw clean package -DskipTests
```

### 2. Set Environment Variables
```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://prod-host:5432/secureapp
export DATABASE_USERNAME=produser
export DATABASE_PASSWORD=prodpassword
export JWT_SECRET=your-production-secret
export MAIL_HOST=smtp.gmail.com
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-password
```

### 3. Run Application
```bash
java -jar target/secure-app-1.0.0.jar
```

## Troubleshooting

### Database Connection Issues
```bash
# Check PostgreSQL is running
sudo systemctl status postgresql

# Test connection
psql -U postgres -d secureapp_dev
```

### Port Already in Use
Change server port in `application.yml`:
```yaml
server:
  port: 8081
```

### Tailwind CSS Not Loading
```bash
# Rebuild CSS
npm run build:css

# Check output file exists
ls src/main/resources/static/css/output.css
```

## Security Best Practices

1. **Change default passwords** immediately
2. **Use strong JWT secrets** in production
3. **Enable HTTPS** in production
4. **Configure secure session cookies**
5. **Implement rate limiting** for login attempts
6. **Regular security updates** for dependencies
7. **Use environment variables** for sensitive data

## License

[Your License Here]

## Support

For issues and questions, please open an issue on the repository.
