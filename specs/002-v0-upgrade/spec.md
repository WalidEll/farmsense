# Feature Specification: v0 Unit Test Upgrade

## Overview

The goal of this feature is to resolve the test environment issues (Java 25 compatibility) and provide comprehensive unit test coverage for the v0 user stories across the backend and frontend.

## Scope

### 1. Backend Environment Fix
- Fix Mockito/Byte Buddy compatibility with Java 25.
- Fix Lombok compatibility with Java 25.

### 2. Backend Service Coverage
- AuthService: Registration, Login, Token Refresh.
- PlantService: CRUD, Threshold management.
- ReadingService: Ingestion, Latest reading, History.
- DeviceService: Provisioning codes, Claiming, Assignment.

### 3. Frontend Component Coverage
- Unit tests for key components: `PlantCard`, `LoginView`, `StatusBadge`.
- Transition to Vitest as the primary test runner.

## Success Criteria

- All backend unit tests pass with `./mvnw test` on Java 25.
- All frontend unit tests pass with `npm run test`.
- All v0 core business logic is covered by unit tests.
