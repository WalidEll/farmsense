# FarmSense Backend — Spring Boot 3

REST API for plant monitoring, IoT sensor ingestion, WhatsApp alerts, and AI diagnosis.

## Stack

- **Spring Boot 3.2** + Java 17
- **PostgreSQL 15** — primary database
- **Redis 7** — token blacklist + caching
- **Flyway** — database migrations
- **Spring Security** + JJWT — JWT authentication
- **Spring WebFlux (WebClient)** — async HTTP for WhatsApp + Claude API
- **Spring Scheduler** — alert threshold checks every 15 minutes

## Project Structure

```
src/main/java/ma/farmsense/
├── FarmSenseApplication.java
├── controller/
│   ├── AuthController.java       # US-001, US-002, US-003
│   ├── PlantController.java      # US-010 → US-014
│   ├── ReadingController.java    # US-020, US-021, US-023
│   ├── DeviceController.java     # US-030 → US-034
│   ├── DiagnoseController.java   # US-050, US-051
│   └── AlertController.java     # US-043, US-044
├── service/
│   ├── AuthService.java
│   ├── PlantService.java
│   ├── ReadingService.java
│   ├── DeviceService.java
│   ├── DiagnoseService.java      # Claude Vision API integration
│   ├── WhatsAppService.java      # Meta WhatsApp Business API
│   └── AlertService.java
├── scheduler/
│   └── AlertScheduler.java       # @Scheduled threshold checker
├── entity/
│   ├── User.java
│   ├── Plant.java
│   ├── SensorReading.java
│   ├── Device.java
│   └── Alert.java
├── repository/
│   ├── UserRepository.java
│   ├── PlantRepository.java
│   ├── SensorReadingRepository.java
│   ├── DeviceRepository.java
│   └── AlertRepository.java
├── security/
│   ├── JwtService.java
│   ├── JwtAuthFilter.java
│   └── SecurityConfig.java
└── config/
    └── WebConfig.java            # CORS configuration
```

## Local Development

```bash
# Start PostgreSQL + Redis
docker-compose up -d

# Run the API
./mvnw spring-boot:run

# API available at http://localhost:8080
# Swagger UI at http://localhost:8080/swagger-ui.html (add springdoc dep to enable)
```

## Authentication

All endpoints except `/api/v1/auth/*` and `POST /api/v1/devices/claim` require:
```
Authorization: Bearer <access_token>
```

ESP32 sensor ingestion uses:
```
X-Device-Key: FS-00042
```

## Alert Schedule

`AlertScheduler` runs every 15 minutes and checks:
- Soil moisture below `plant.soil_min` for 2 consecutive readings → `SOIL_DRY` alert
- Temperature above `plant.temp_max` → `TEMP_HIGH` alert
- Temperature below `plant.temp_min` → `TEMP_LOW` alert
- Light below `plant.light_min` during daylight hours → `LIGHT_LOW` alert

Alerts are suppressed for 4 hours after firing (configurable via `farmsense.alert.suppress-hours`).

## Database Migrations

Flyway migrations in `src/main/resources/db/migration/`:
- `V1__initial_schema.sql` — users, plants, devices, sensor_readings, alerts, diagnoses, care_log
