# FarmSense — Codex Context

## Project Overview

FarmSense is a smart plant monitoring platform targeting Moroccan home plant owners (v0) and small farms/greenhouses (v1). It combines IoT sensors (ESP32), a REST API (Spring Boot), and a PWA (Vue 3) to monitor plants and send real-time WhatsApp alerts in Darija, Arabic, and French. AI-powered plant diagnosis uses the Codex Vision API.

## Monorepo Structure

```
farmsense/
├── apps/
│   ├── frontend/     # Vue 3 PWA (TypeScript, Vite, Tailwind)
│   ├── backend/      # Spring Boot 3 REST API (Java 17)
│   └── esp32/        # Arduino firmware (C++)
└── docs/
    └── USER_STORIES_V0.md  # 71 user stories across 8 epics
```

Each app is independently built and deployed.

---

## Backend (`apps/backend`)

### Tech Stack
- Java 17, Spring Boot 3.2.5, Maven
- PostgreSQL 15, Redis 7, Flyway migrations
- Spring Security + JJWT 0.12.5 (Redis token blacklist)
- Spring WebFlux for async calls (WhatsApp + Codex APIs)

### Running Locally
```bash
cd apps/backend
docker-compose up -d          # Start PostgreSQL + Redis
./mvnw spring-boot:run        # API at http://localhost:8080
```

API base path: `http://localhost:8080/api/v1`

### Architecture
**Layered:** Controllers → Services → Repositories (JPA)

| Layer | Files |
|-------|-------|
| Controllers | `AuthController`, `PlantController`, `ReadingController`, `DeviceController`, `DiagnoseController`, `AlertController` |
| Services | `AuthService`, `PlantService`, `ReadingService`, `DeviceService`, `DiagnoseService`, `WhatsAppService`, `AlertService` |
| Entities | `User`, `Plant`, `Device`, `SensorReading`, `Alert` |
| Scheduler | `AlertScheduler` — runs every 15 min, checks thresholds |

### Environment Variables
```
DB_URL=jdbc:postgresql://localhost:5432/farmsense
DB_USER=farmsense
DB_PASSWORD=farmsense
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=<256-bit-secret>
WHATSAPP_TOKEN=<meta-api-token>
WHATSAPP_PHONE_ID=<meta-phone-id>
ANTHROPIC_API_KEY=<Codex-api-key>
```

### Config File
`src/main/resources/application.yml`

### Database Migrations
`src/main/resources/db/migration/` — Flyway SQL files (prefix `V{n}__`)

---

## Frontend (`apps/frontend`)

### Tech Stack
- Vue 3 (Composition API), TypeScript, Vite 5
- Pinia (state), Tailwind CSS, Chart.js + vue-chartjs
- Workbox (service worker), PouchDB (offline queue)
- i18n: Darija / Arabic / French with RTL support

### Running Locally
```bash
cd apps/frontend
npm install
npm run dev        # Dev server at http://localhost:5173
```

### Scripts
| Command | Purpose |
|---------|---------|
| `npm run dev` | Dev server |
| `npm run build` | Production build |
| `npm run preview` | Preview production build |
| `npm run lint` | ESLint |
| `npm run type-check` | TypeScript check |

### Architecture
- **Stores** (`src/stores/`): `auth.store.ts`, `plants.store.ts`, `readings.store.ts`, `alerts.store.ts`
- **Services** (`src/services/`): `api.ts` (Axios + JWT auto-refresh), `offline-queue.ts` (PouchDB sync)
- **Components** (`src/components/`): organized by domain — `plants/`, `sensors/`, `alerts/`, `auth/`, `shared/`
- **i18n** (`src/i18n/`): translation files + RTL detection

### Environment Variables
```
VITE_API_URL=http://localhost:8080/api/v1
```

### Offline-First Pattern
- Workbox caches GET responses from the API
- PouchDB queues POST/PUT requests made while offline and replays them on reconnect
- `shared/OfflineBanner.vue` shows offline status

---

## ESP32 Firmware (`apps/esp32`)

### Tech Stack
- Arduino C++ (IDE 2.0+), ESP32 DevKit V1
- Sensors: DHT22 (temp/humidity), BH1750 (light), capacitive soil moisture

### Required Libraries (Arduino Library Manager)
- `DHT sensor library` (Adafruit)
- `BH1750`
- `WiFiManager`
- `ArduinoJson`
- `ArduinoOTA`

### Flashing
Open `src/main.ino` in Arduino IDE and flash to the device.

### Device Provisioning
After power-on, the device broadcasts a WiFi AP. Connect and open `192.168.4.1` to configure WiFi + backend URL.

Batch provisioning (stamps device IDs at compile time):
```bash
python scripts/stamp-device.py --start 1 --count 50
```

### Communication
Devices POST sensor readings with `X-Device-Key` header for authentication.

---

## Key Architectural Decisions

| Decision | Detail |
|----------|--------|
| JWT auth | Access + refresh tokens; Redis blacklist for logout |
| Async I/O | Spring WebFlux used only for WhatsApp + Codex API calls |
| Alert scheduler | `@Scheduled` every 15 min; checks readings against per-plant thresholds |
| Offline sync | PouchDB queues mutations; replays in order after reconnect |
| Multilingual | FR / AR / Darija; RTL toggled via i18n locale |
| AI diagnosis | `DiagnoseService` calls Codex Vision API; results stored + retrievable |

---

## User Stories Reference

71 user stories across 8 epics are in `docs/USER_STORIES_V0.md`:
1. Authentication & Onboarding
2. Plant Profiles & Care Schedules
3. IoT Sensor Dashboard
4. Device Provisioning
5. Smart Alerts
6. AI Plant Diagnosis
7. Offline & PWA
8. Multilingual UI

## Active Technologies
- Markdown (document artifact); no runtime language required + `docs/USER_STORIES_V0.md` (source of truth for 26 stories + acceptance criteria); existing codebase in `apps/backend`, `apps/frontend`, `apps/esp32` (001-v0-us-coverage)
- File system — `specs/001-v0-us-coverage/coverage-report.md` (001-v0-us-coverage)

## Recent Changes
- 001-v0-us-coverage: Added Markdown (document artifact); no runtime language required + `docs/USER_STORIES_V0.md` (source of truth for 26 stories + acceptance criteria); existing codebase in `apps/backend`, `apps/frontend`, `apps/esp32`
