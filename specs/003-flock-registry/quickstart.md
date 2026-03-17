# Quickstart: Flock Registry (003)

## Prerequisites

- Docker Desktop running
- Java 21, Maven
- Node 20+, npm

## Run the stack

```bash
# Backend
cd apps/backend
docker-compose up -d       # starts PostgreSQL + Redis
./mvnw spring-boot:run     # applies V19 migration automatically on startup
```

```bash
# Frontend
cd apps/frontend
npm install
npm run dev                # http://localhost:5173
```

## Verify the feature

### 1. Create a housing location

```bash
curl -X POST http://localhost:8080/api/v1/housing-locations \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Coop A","locationType":"COOP"}'
```

### 2. Register a flock

```bash
curl -X POST http://localhost:8080/api/v1/flocks \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Spring Layers",
    "batchCode": "FL-2026-001",
    "breedId": "<isa-brown-id>",
    "purpose": "LAYERS",
    "birdCount": 500,
    "startDate": "2026-01-06",
    "housingLocationId": "<coop-a-id>"
  }'
```

Expected response includes `ageWeeks: 10` (10 weeks since Jan 6).

### 3. Log a mortality event

```bash
curl -X POST http://localhost:8080/api/v1/flocks/<flock-id>/mortality-events \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "NATURAL_DEATH",
    "count": 5,
    "mortalityDate": "2026-03-14",
    "cause": "DISEASE"
  }'
```

Expected: `updatedFlockHeadcount: 495`.

### 4. Verify headcount updated

```bash
curl http://localhost:8080/api/v1/flocks/<flock-id> \
  -H "Authorization: Bearer <token>"
# currentBirdCount should now be 495
```

## Run tests

```bash
# Backend unit tests
cd apps/backend && ./mvnw test

# Frontend unit tests
cd apps/frontend && npm run test

# Playwright E2E
cd apps/frontend && npx playwright test e2e/poultry.spec.ts
```
