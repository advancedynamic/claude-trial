# Docker Deployment Guide

This guide explains how to run the Secure App using Docker and Docker Compose.

## Prerequisites

- **Docker** 20.10 or higher
- **Docker Compose** 2.0 or higher
- **Node.js & npm** (for building Tailwind CSS)

Install Docker:
```bash
# Check Docker installation
docker --version
docker-compose --version
```

## Quick Start

### 1. Environment Setup

Create your environment file:
```bash
cp .env.example .env
```

Edit `.env` and update the values as needed:
```env
POSTGRES_PASSWORD=your_secure_password
JWT_SECRET=your_very_long_random_secret_key
```

### 2. Build Tailwind CSS

Before building the Docker image, compile the CSS:
```bash
npm install
npm run build:css
```

### 3. Start All Services

Using the helper script:
```bash
./docker/scripts/start.sh
```

Or manually:
```bash
docker-compose up -d --build
```

### 4. Access the Application

- **Application:** http://localhost:8080
- **MailHog (Email UI):** http://localhost:8025
- **Database:** localhost:5432

Default credentials:
- **Admin:** username=`admin`, password=`admin123`
- **User:** username=`user`, password=`user123`

## Services

The Docker Compose stack includes three services:

### 1. Application (Spring Boot)
- **Container:** `secureapp`
- **Port:** 8080
- **Health Check:** `/actuator/health`
- **Logs:** `/app/logs/application.log`

### 2. PostgreSQL Database
- **Container:** `secureapp-postgres`
- **Port:** 5432
- **Database:** `secureapp`
- **Volume:** `postgres_data`

### 3. MailHog (Email Testing)
- **Container:** `secureapp-mailhog`
- **SMTP Port:** 1025
- **Web UI:** 8025

## Docker Commands

### View Logs

All services:
```bash
docker-compose logs -f
```

Specific service:
```bash
docker-compose logs -f app
docker-compose logs -f postgres
docker-compose logs -f mailhog
```

Or use the helper script:
```bash
./docker/scripts/logs.sh app
```

### Stop Services

```bash
docker-compose down
```

Or use the helper script:
```bash
./docker/scripts/stop.sh
```

### Restart Services

```bash
docker-compose restart
```

Restart specific service:
```bash
docker-compose restart app
```

### Rebuild Application

After code changes:
```bash
./docker/scripts/rebuild.sh
```

Or manually:
```bash
docker-compose up -d --build app
```

### Stop and Remove Everything (including volumes)

```bash
docker-compose down -v
```

⚠️ **Warning:** This will delete all database data!

## Database Access

### Connect to PostgreSQL

Using Docker:
```bash
docker exec -it secureapp-postgres psql -U postgres -d secureapp
```

Using local psql client:
```bash
psql -h localhost -p 5432 -U postgres -d secureapp
```

### Database Backup

```bash
docker exec secureapp-postgres pg_dump -U postgres secureapp > backup.sql
```

### Database Restore

```bash
cat backup.sql | docker exec -i secureapp-postgres psql -U postgres -d secureapp
```

## Troubleshooting

### Check Service Status

```bash
docker-compose ps
```

### Check Service Health

```bash
docker-compose ps
docker inspect secureapp | grep -A 10 Health
```

### Application Won't Start

1. Check logs:
```bash
docker-compose logs app
```

2. Verify database is healthy:
```bash
docker-compose ps postgres
```

3. Check environment variables:
```bash
docker-compose config
```

### Database Connection Issues

1. Ensure PostgreSQL is running:
```bash
docker-compose ps postgres
```

2. Test connection:
```bash
docker exec secureapp-postgres pg_isready -U postgres
```

3. Check application logs:
```bash
docker-compose logs app | grep -i database
```

### Port Already in Use

If port 8080 is already in use, change it in `.env`:
```env
APP_PORT=8081
```

Then restart:
```bash
docker-compose down
docker-compose up -d
```

### CSS Not Loading

Rebuild Tailwind CSS:
```bash
npm run build:css
docker-compose up -d --build app
```

## Development Workflow

### Code Changes

1. Make your code changes
2. Rebuild the app container:
```bash
./docker/scripts/rebuild.sh
```
3. Check logs:
```bash
./docker/scripts/logs.sh app
```

### Database Migrations

Flyway automatically runs migrations on startup. To create a new migration:

1. Create new SQL file in `src/main/resources/db/migration/`:
```
V3__your_migration_name.sql
```

2. Rebuild and restart:
```bash
docker-compose up -d --build app
```

### Environment Variables

Update `.env` file and restart services:
```bash
docker-compose down
docker-compose up -d
```

## Production Deployment

### Using Docker Compose

1. Update `.env` for production:
```env
POSTGRES_PASSWORD=strong_production_password
JWT_SECRET=very_long_random_production_secret
APP_BASE_URL=https://yourdomain.com
```

2. Use production mail server instead of MailHog:
```yaml
# In docker-compose.yml, update app service environment:
SPRING_MAIL_HOST: smtp.gmail.com
SPRING_MAIL_PORT: 587
SPRING_MAIL_USERNAME: ${MAIL_USERNAME}
SPRING_MAIL_PASSWORD: ${MAIL_PASSWORD}
```

3. Start services:
```bash
docker-compose up -d --build
```

### Using Docker Swarm or Kubernetes

For production orchestration, see:
- Docker Swarm: [Docker Swarm Guide](https://docs.docker.com/engine/swarm/)
- Kubernetes: [Kubernetes Guide](https://kubernetes.io/docs/)

## Performance Tuning

### Application Memory

Adjust JVM memory in Dockerfile:
```dockerfile
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Xms512m", "-Xmx1024m", "-jar", "app.jar"]
```

### Database Connection Pool

Update `application-docker.yml`:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
```

### PostgreSQL Performance

Update `docker-compose.yml`:
```yaml
postgres:
  command: postgres -c max_connections=200 -c shared_buffers=256MB
```

## Security Best Practices

1. **Change default passwords** in `.env`
2. **Use secrets management** in production (Docker secrets, Kubernetes secrets)
3. **Enable HTTPS** with a reverse proxy (nginx, Traefik)
4. **Run as non-root user** (already configured in Dockerfile)
5. **Scan images** for vulnerabilities:
```bash
docker scan secureapp:latest
```
6. **Limit resources**:
```yaml
app:
  deploy:
    resources:
      limits:
        cpus: '1.0'
        memory: 1G
```

## Monitoring

### Health Checks

Application health:
```bash
curl http://localhost:8080/actuator/health
```

Database health:
```bash
docker exec secureapp-postgres pg_isready
```

### Metrics

Access metrics endpoint:
```bash
curl http://localhost:8080/actuator/metrics
```

### Container Stats

```bash
docker stats secureapp secureapp-postgres secureapp-mailhog
```

## Cleanup

### Remove All Containers and Images

```bash
docker-compose down
docker rmi secureapp:latest
docker system prune -a
```

### Remove Volumes (⚠️ Deletes all data)

```bash
docker-compose down -v
docker volume rm secureapp_postgres_data secureapp_app_logs
```

## Support

For issues and questions:
- Check logs: `docker-compose logs`
- Review documentation: `README.md`
- Open an issue on the repository
