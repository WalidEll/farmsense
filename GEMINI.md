# farmsense Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-03-12

## Active Technologies
- Java 17/25, TypeScript/Vites + Mockito, JUnit 5, Vitest, @vue/test-utils (002-v0-upgrade)
- Java 21 LTS (backend), TypeScript strict (frontend) + Spring Boot 3.4.x, Vue 3 (Composition API), Pinia, Tailwind CSS 4 (002-breed-catalogue)
- PostgreSQL 15 via Flyway migrations, Redis 7 (auth only) (002-breed-catalogue)

- Markdown (document artifact); no runtime language required + `docs/USER_STORIES_V0.md` (source of truth for 26 stories + acceptance criteria); existing codebase in `apps/backend`, `apps/frontend`, `apps/esp32` (001-v0-us-coverage)

## Project Structure

```text
src/
tests/
```

## Commands

# Add commands for Markdown (document artifact); no runtime language required

## Code Style

Markdown (document artifact); no runtime language required: Follow standard conventions

## Recent Changes
- 002-breed-catalogue: Added Java 21 LTS (backend), TypeScript strict (frontend) + Spring Boot 3.4.x, Vue 3 (Composition API), Pinia, Tailwind CSS 4
- 002-v0-upgrade: Added Java 17/25, TypeScript/Vites + Mockito, JUnit 5, Vitest, @vue/test-utils

- 001-v0-us-coverage: Added Markdown (document artifact); no runtime language required + `docs/USER_STORIES_V0.md` (source of truth for 26 stories + acceptance criteria); existing codebase in `apps/backend`, `apps/frontend`, `apps/esp32`

<!-- MANUAL ADDITIONS START -->

## Monorepo Structure

```text
farmsense/
├── apps/
│   ├── frontend/     # Vue 3 PWA (TypeScript, Vite, Tailwind 4)
│   ├── backend/      # Spring Boot 3.4.x REST API (Java 21)
│   └── esp32/        # Arduino firmware (C++)
├── specs/            # Feature specifications and plans
│   ├── 001-v0-us-coverage/
│   └── 002-breed-catalogue/   # Active feature
└── docs/
```

## Architecture Rules (from Constitution v3.1.0)

These are NON-NEGOTIABLE — every implementation MUST follow:

1. **Backend Layered**: Controller → Service → Repository. Controllers MUST NOT call repositories directly. Controllers MUST NOT mutate entity state.
2. **Records Mandate**: ALL DTOs MUST be Java records. Use `static from()` factory for entity mapping. No Lombok `@Data`/`@Builder` in `dto/` packages. Lombok is permitted for entities only.
3. **Offline-First**: Workbox caches GETs. PouchDB queues writes offline with replay on reconnect.
4. **Multilingual**: FR/AR/EN with RTL. Translation keys added at implementation time — never deferred.
5. **Security**: JWT auth required. User-scoped data. Jakarta validation on all DTOs.
6. **Testing**: JUnit 5 + Mockito (backend), Vitest (frontend), Playwright E2E.
7. **CSS-First**: Tailwind 4 via `@theme` in CSS. No JS tailwind config.
8. **Virtual Threads**: `spring.threads.virtual.enabled: true`. No manual thread pools.

## Backend Patterns (follow exactly)

- **Entities**: Lombok `@Entity @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor`, UUID PK, `@PreUpdate` for updatedAt, `Instant` for timestamps
- **DTOs**: Java records, `static from(Entity)` factory, Jakarta validation (`@NotBlank`, `@Size`, `@Valid`)
- **Services**: `@Service @RequiredArgsConstructor`, `@Transactional` on writes, `getOwned(User, UUID)` pattern for ownership checks
- **Controllers**: `@RestController @RequestMapping("/api/v1/...")`, `@AuthenticationPrincipal User`, `@Valid @RequestBody`
- **Repositories**: `JpaRepository<Entity, UUID>` with Spring Data derived query methods

## Frontend Patterns (follow exactly)

- **Stores**: Pinia composition API (`defineStore` with `setup` function)
- **Components**: `<script setup lang="ts">`, props + emits, no direct API calls
- **Views**: Route-level pages that orchestrate stores
- **i18n**: Keys in `src/i18n/{fr,ar,en}.ts`, use `$t('key')` in templates

## Current Feature: 002-breed-catalogue

All design artifacts are in `specs/002-breed-catalogue/`:
- `spec.md` — Feature specification with 5 user stories
- `plan.md` — Technical implementation plan
- `data-model.md` — Entity schemas and relationships
- `contracts/breed-catalogue-api.md` — REST API contracts
- `research.md` — Technical decisions
- `tasks.md` — 49 implementation tasks in dependency order

GitHub Issues #4–#52 track each task with labels `breed-catalogue`, phase, and priority.

<!-- MANUAL ADDITIONS END -->
