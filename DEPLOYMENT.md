# FarmSense Deployment Guide (TrueNAS)

This guide explains how to deploy FarmSense to a **TrueNAS SCALE** instance using **GitHub Actions** and a **Self-Hosted Runner**.

## 1. Prepare TrueNAS Environment

You need a Linux environment with Docker and Docker Compose. On TrueNAS SCALE, you have two main options:
- **A Linux VM** (Ubuntu Server recommended): Most stable and isolated.
- **A Sandbox/Jail (systemd-nspawn)**: More lightweight but requires manual Docker setup.

### Prerequisites (on the VM/Host)
1. **Install Docker & Docker Compose**:
   ```bash
   sudo apt update
   sudo apt install docker.io docker-compose-v2 -y
   sudo usermod -aG docker $USER
   newgrp docker
   ```
2. **Setup Data Persistence**:
   Create a directory for your data if you want to mount TrueNAS datasets:
   ```bash
   mkdir -p ~/farmsense/data
   ```

## 2. Setup GitHub Self-Hosted Runner

1. Go to your GitHub Repository: **Settings > Actions > Runners**.
2. Click **New self-hosted runner**.
3. Select **Linux** and **X64**.
4. Follow the download and configuration instructions on your TrueNAS VM.
   - When asked for the runner group, use `Default`.
   - When asked for the name, use something like `truenas-runner`.
   - When asked for labels, the default `self-hosted` is used in our workflow.
5. **Install as a service** (so it starts on boot):
   ```bash
   sudo ./svc.sh install
   sudo ./svc.sh start
   ```

## 3. Configure GitHub Secrets

Go to **Settings > Secrets and variables > Actions** and add the following secrets:

| Secret Name | Description | Example |
|-------------|-------------|---------|
| `DB_USER` | PostgreSQL Username | `farmsense` |
| `DB_PASSWORD` | PostgreSQL Password | `a-strong-password` |
| `JWT_SECRET` | 256-bit secret for JWT | `base64-random-string...` |
| `WHATSAPP_TOKEN` | Meta API Token | `EAAB...` |
| `WHATSAPP_PHONE_ID` | Meta Phone Number ID | `123456789` |
| `ANTHROPIC_API_KEY` | Claude API Key | `sk-ant-api03-...` |

## 4. Deployment Pipeline

We use two separate workflows for a robust CI/CD:

1.  **Build & Push (`docker-build-push.yml`)**:
    - Runs on GitHub-hosted runners.
    - Builds multi-stage Docker images for Backend and Frontend.
    - Pushes images to **GitHub Container Registry (GHCR)**: `ghcr.io/owner/farmsense-backend:latest`.
    - Requires **Package Write Permissions** (enabled by default for the `GITHUB_TOKEN`).

2.  **Deploy to TrueNAS (`deploy.yml`)**:
    - Triggered after the build workflow completes.
    - Runs on your **Self-Hosted Runner** on TrueNAS.
    - Pulls pre-built images from GHCR.
    - Restarts containers using `docker compose up -d`.

**Workflow Steps:**
1. **Checkout**: Pulls the latest code.
2. **Login**: Authenticates the self-hosted runner to GHCR using the `GITHUB_TOKEN`.
3. **Environment**: Generates a `.env` file from GitHub Secrets.
4. **Docker Compose**: 
   - Pulls latest images from GHCR.
   - Starts all services in detached mode.
   - Prunes old unused images to save disk space on TrueNAS.

## 5. Accessing the Application

Once deployed, the application will be available at your TrueNAS VM's IP address on **Port 80**.
- **Frontend**: `http://<VM_IP>/`
- **Backend API**: `http://<VM_IP>/api/v1`

## 6. Troubleshooting

- **Check Logs**:
  ```bash
  docker compose logs -f backend
  ```
- **Database Access**:
  The DB is exposed on port `5432` by default. You can change this in the root `docker-compose.yml`.
- **Nginx Proxy**:
  The frontend container includes an Nginx config that proxies `/api` requests to the `backend` container.
