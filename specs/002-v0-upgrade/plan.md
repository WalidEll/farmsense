# Implementation Plan: v0 Unit Test Upgrade

**Branch**: `feat/unit-tests-v0` | **Date**: 2026-03-13

## Summary

This plan addresses the test failures on Java 25 and provides the foundation for v0 unit test coverage across the backend and frontend.

## Technical Context

**Language/Version**: Java 17/25, TypeScript/Vitest
**Primary Dependencies**: Mockito, JUnit 5, Vitest, @vue/test-utils

## Tasks

### 1. Fix Backend Environment (DONE)
- [x] Update `pom.xml` with `maven-surefire-plugin` property `-Dnet.bytebuddy.experimental=true`.
- [x] Upgrade `lombok.version` to `1.18.44` for Java 25 support.

### 2. Expand Backend Service Coverage (DONE)
- [x] Update DTOs with `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` (Lombok).
- [x] Create `AuthServiceTest.java`.
- [x] Create `PlantServiceTest.java`.
- [x] Create `ReadingServiceTest.java`.
- [x] Create `DeviceServiceTest.java`.

### 3. Frontend Unit Test Transition (DONE)
- [x] Verify existing `StatusBadge.spec.ts`.
- [x] Create `PlantCard.spec.ts`.
- [x] Create `LoginView.spec.ts`.

## Verification

- [x] `./mvnw test` in `apps/backend` (25 tests passed).
- [x] `npm run test` in `apps/frontend` (13 tests passed).
