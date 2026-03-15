# FarmSense — Monorepo

Smart plant monitoring platform for Morocco. v0 targets home plant owners; v1 targets small farms and greenhouses.

```
farmsense/
├── apps/
│   ├── frontend/     # Vue 3 PWA — offline-first, Arabic/Darija RTL
│   ├── backend/      # Spring Boot 3 REST API — JWT, multi-tenant
│   └── esp32/        # Arduino C++ firmware — WiFiManager, sensors, OTA
└── docs/
    └── USER_STORIES_V0.md
```

## Quick Start

### Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ |
| Node.js | 20+ |
| Docker | 24+ |
| Arduino IDE | 2.0+ |
| PostgreSQL | 15 (via Docker) |

### 1 — Backend

```bash
cd apps/backend
docker-compose up -d          # starts PostgreSQL + Redis
./mvnw spring-boot:run
# API available at http://localhost:8080
```

### 2 — Frontend

```bash
cd apps/frontend
npm install
npm run dev
# PWA available at http://localhost:5173
```

### 3 — ESP32

Open `apps/esp32/src/main.ino` in Arduino IDE.  
Install required libraries (see `apps/esp32/README.md`).  
Flash to your ESP32 DevKit V1.

## Architecture

```
[ESP32 Sensors] ──POST /api/v1/readings──► [Spring Boot API]
                                                    │
[Vue PWA] ◄───────────── REST + JWT ───────────────┤
                                                    │
[WhatsApp Business API] ◄── AlertScheduler ─────────┤
                                                    │
[Claude Vision API] ◄──── /api/v1/diagnose ─────────┘
```

## Environment Variables

Copy `.env.example` to `.env` in each app directory and fill in values.

| Variable | Description |
|----------|-------------|
| `DB_URL` | PostgreSQL JDBC URL |
| `JWT_SECRET` | 256-bit secret for signing tokens |
| `WHATSAPP_TOKEN` | Meta WhatsApp Business API token |
| `ANTHROPIC_API_KEY` | Claude API key for plant diagnosis |
| `OTA_PASSWORD` | ESP32 OTA update password |

## Branching Strategy

```
main          ← production (tagged releases)
develop       ← integration branch
feature/*     ← individual features
hotfix/*      ← production fixes
```

## License

Proprietary — Domaine Al Khair / FarmSense 2025
