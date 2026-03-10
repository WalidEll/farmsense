# FarmSense Deployment Guide (Dockge on TrueNAS)

This guide explains how to deploy FarmSense manually on a **TrueNAS SCALE** instance using **Dockge** and images built via **GitHub Actions**.

## 1. Automated Build Pipeline

The project uses GitHub Actions (`.github/workflows/docker-build-push.yml`) to automatically build and push Docker images to the **GitHub Container Registry (GHCR)** whenever code is pushed to the `main` branch.

**Registry Images:**
- `ghcr.io/<your-github-username>/farmsense-backend:latest`
- `ghcr.io/<your-github-username>/farmsense-frontend:latest`

## 2. Prepare TrueNAS for Dockge

If you haven't installed Dockge yet:
1. Install it via TrueNAS "Apps" (it's available in the official or TrueCharts catalog).
2. Alternatively, run it in a Linux VM/Sandbox using their [official install script](https://dockge.kuma.pet/).

## 3. Manual Deployment with Dockge

1. Open your **Dockge** dashboard.
2. Click **+ Compose** to create a new stack named `farmsense`.
3. Copy and paste the following `docker-compose.yml` (update the `<username>` placeholder):

```yaml
version: '3.9'

services:
  # Database
  postgres:
    image: postgres:15-alpine
    container_name: farmsense-db
    environment:
      POSTGRES_DB: farmsense
      POSTGRES_USER: ${DB_USER:-farmsense}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-farmsense}
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER:-farmsense}"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # Redis for cache & blacklist
  redis:
    image: redis:7-alpine
    container_name: farmsense-redis
    ports:
      - "6379:6379"
    command: redis-server --maxmemory 128mb --maxmemory-policy allkeys-lru
    restart: unless-stopped

  # Backend API
  backend:
    image: ghcr.io/<username>/farmsense-backend:latest
    container_name: farmsense-backend
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:postgresql://postgres:5432/farmsense
      DB_USER: ${DB_USER:-farmsense}
      DB_PASSWORD: ${DB_PASSWORD:-farmsense}
      REDIS_HOST: redis
      JWT_SECRET: ${JWT_SECRET}
      WHATSAPP_TOKEN: ${WHATSAPP_TOKEN}
      WHATSAPP_PHONE_ID: ${WHATSAPP_PHONE_ID}
      ANTHROPIC_API_KEY: ${ANTHROPIC_API_KEY}
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
    restart: unless-stopped

  # Frontend Web
  frontend:
    image: ghcr.io/<username>/farmsense-frontend:latest
    container_name: farmsense-frontend
    ports:
      - "8080:80" # Map to 8080 or any preferred port on TrueNAS
    depends_on:
      - backend
    restart: unless-stopped

volumes:
  pgdata:
```

## 4. Configure Environment Variables

In Dockge, click the **.env** tab or use the UI to add the following variables:

| Variable Name | Example |
|-------------|---------|
| `DB_USER` | `farmsense` |
| `DB_PASSWORD` | `your-secure-db-pass` |
| `JWT_SECRET` | `your-256-bit-jwt-secret` |
| `WHATSAPP_TOKEN` | `EAAB...` |
| `WHATSAPP_PHONE_ID` | `123456789` |
| `ANTHROPIC_API_KEY` | `sk-ant-api03-...` |

## 5. Deployment

1. Click **Deploy** in Dockge.
2. To update to the latest version after a new GitHub build:
   - Click **Update** in Dockge (this will pull the latest images and recreate containers).

## 6. Registry Authentication (If Private)

If your GHCR images are private, you must run this command once on your TrueNAS host to allow Docker to pull them:
```bash
docker login ghcr.io -u <your-github-username>
# Use a GitHub Personal Access Token (PAT) with 'read:packages' scope as the password
```
