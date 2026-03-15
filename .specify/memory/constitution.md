<!--
Sync Impact Report
- Version change: 3.0.0 → 3.1.0
- Modified principles: None
- Added sections: None
- Removed sections: None
- Modified sections:
  - "Development Workflow" — specs tracked via GitHub Issues instead of
    local `specs/` markdown files; `docs/USER_STORIES_V0.md` remains as
    reference but GitHub Issues are the source of truth for active work
- Templates requiring updates:
  - .specify/templates/plan-template.md ⚠ references local specs/ paths —
    update Input/Documentation sections to reference GitHub Issues
  - .specify/templates/spec-template.md ✅ no changes needed (generic)
  - .specify/templates/tasks-template.md ⚠ references local specs/ paths —
    update Input/Prerequisites to reference GitHub Issues
- Follow-up TODOs: None
-->

# FarmSense Constitution

## Core Principles

### I. Offline-First

Every user-facing feature MUST degrade gracefully without network
connectivity. The PWA MUST cache the last-fetched plant list and
sensor readings via Workbox. All write operations performed offline
MUST be queued in PouchDB/IndexedDB and replayed automatically on
reconnect with retry and exponential backoff. Offline state MUST be
clearly communicated to the user via an `OfflineBanner` component.

### II. Multilingual & RTL by Design

All UI text, error messages, alerts, and WhatsApp notifications MUST
be available in French, Arabic, and Darija. Arabic and Darija locales
MUST trigger full RTL layout (text alignment, icon positions,
navigation direction). Translation keys MUST be added for every new
user-facing string at the time of implementation — never deferred.
The i18n system in `src/i18n/` is the single source of truth for all
translatable content.

### III. Clean Layered Architecture (NON-NEGOTIABLE)

**Backend** MUST follow the Controller → Service → Repository pattern
with strict layer boundaries:

- **Controllers** handle HTTP concerns only: request validation,
  authentication principal extraction, delegation to services, and
  response mapping. Controllers MUST NOT inject or call repositories
  directly. Controllers MUST NOT mutate entity state.
- **Services** contain all business logic and orchestrate repository
  calls. Every domain area MUST have a dedicated service class.
- **Repositories** handle persistence via JPA. Custom queries MUST
  use Spring Data derived methods or `@Query` annotations.
- **DTOs** MUST be Java records (see Principle VIII). Entity objects
  MUST NOT be exposed in controller method signatures or returned in
  API responses (except `@AuthenticationPrincipal` for the current
  user). DTO ↔ Entity mapping uses static factory methods on records.

**Frontend** MUST separate concerns into:

- **Views** (route-level pages in `views/`)
- **Stores** (Pinia, composition API style, in `stores/`)
- **Services** (API client, offline queue, in `services/`)
- **Components** (organized by domain in `components/`, shared UI
  in `components/shared/`)

Views orchestrate stores. Stores call services. Components receive
props and emit events — no direct API calls from components.

### IV. API-Contract First

Every feature that involves frontend-backend communication MUST define
its REST endpoint contract before implementation begins. API endpoints
live under `/api/v1/`. Request/response DTOs MUST be explicit Java
records — no raw `Map` or `JsonNode` types in controller signatures.
Breaking API changes MUST increment the version path segment.

### V. Security by Default

Authentication MUST use JWT access + refresh tokens with Redis-backed
blacklisting. All API calls (except auth endpoints) MUST require a
valid `Authorization: Bearer` header. Passwords MUST be hashed with
bcrypt. Device authentication MUST use `X-Device-Key` headers.
Sensitive configuration (API keys, secrets) MUST be externalized via
environment variables — never committed to source control. SQL queries
MUST use parameterized statements (JPA handles this; raw queries are
prohibited unless justified and reviewed). All controller inputs MUST
be validated using Jakarta Bean Validation annotations (`@Valid`,
`@NotBlank`, `@Size`, etc.) on record components.

### VI. Playwright E2E Testing (NON-NEGOTIABLE)

Every user-facing feature MUST have corresponding Playwright end-to-end
tests covering its critical user journeys. E2E tests live in
`apps/frontend/e2e/` and are organized by feature domain:

```
apps/frontend/e2e/
├── auth.setup.ts    # Auth state setup
├── auth.spec.ts     # Login, register flows
├── dashboard.spec.ts
├── plants.spec.ts
├── plant-detail.spec.ts
├── sensors.spec.ts
├── alerts.spec.ts
├── crops.spec.ts
├── plans.spec.ts
├── accounting.spec.ts
├── inventory.spec.ts
├── poultry.spec.ts
├── settings.spec.ts
└── i18n.spec.ts     # Language switching, RTL
```

**Test requirements:**
- Each E2E test MUST be independent and idempotent (no test ordering
  dependencies).
- Tests MUST cover the happy path and at least one error/edge case
  per feature.
- Authentication flows MUST be tested with real login (not bypassed)
  in at least one suite; other suites MAY use `storageState` for
  speed.
- New features MUST NOT be merged without passing Playwright tests.
- Playwright config MUST be at `apps/frontend/playwright.config.ts`.

### VII. Test Coverage Discipline

Testing is mandatory at multiple levels:

**Backend:**
- Every service class MUST have unit tests (JUnit 5 + Mockito).
- Integration tests (`@SpringBootTest` with H2 or Testcontainers)
  MUST cover each REST endpoint's happy path and error cases.

**Frontend:**
- Critical UI components MUST have Vitest unit tests for rendering
  logic and prop/event contracts.
- Pinia stores MUST have unit tests for state mutations and API
  call orchestration.
- Playwright E2E tests (see Principle VI) cover user journey
  validation.

**Test gates:**
- All tests MUST pass before a PR can be merged.
- New code MUST include tests for new behavior; bug fixes MUST
  include a regression test.

### VIII. Records Mandate (NON-NEGOTIABLE)

All DTOs (request and response objects in `dto/` packages) MUST be
Java records. This eliminates Lombok `@Data`/`@Builder` boilerplate
and leverages Java 21's built-in capabilities:

- **Response DTOs** use a `static from()` factory method for entity
  mapping.
- **Request DTOs** use Jakarta validation annotations directly on
  record components.
- **Entities** keep Lombok — they are JPA-managed, not DTOs.
- No `@Data`, `@Builder`, `@NoArgsConstructor`, or
  `@AllArgsConstructor` in `dto/` packages.

### IX. CSS-First Rule

All custom styling MUST use Tailwind 4's CSS-first configuration via
`@theme` blocks in `main.css`. No JavaScript-based Tailwind config
files (`tailwind.config.js`). PostCSS is handled internally by
Tailwind 4 — no separate `postcss.config.js`.

### X. Concurrency Standard

Virtual threads MUST be enabled for all request handling
(`spring.threads.virtual.enabled: true`). No manual Platform Thread
management or custom thread pools for I/O-bound work. Spring WebFlux
remains reserved exclusively for WhatsApp and Claude API calls.

### XI. Bundler Integrity

No custom Rollup or Esbuild plugins that bypass Vite 6's unified
build pipeline. The `@tailwindcss/vite` plugin is the sole approved
addition to the default Vite plugin chain (alongside `@vitejs/plugin-vue`
and `vite-plugin-pwa`).

## Technology Constraints

- **Backend**: Java 21 LTS, Spring Boot 3.4.x (MUST stay on latest
  stable 3.x minor), Maven, PostgreSQL 15, Redis 7, Flyway
  migrations. Virtual threads enabled. Async I/O (WebFlux) is
  reserved exclusively for WhatsApp and Claude API calls — all other
  endpoints use blocking Spring MVC. All DTOs MUST be Java records.
  Lombok is permitted for entities only.
- **Frontend**: Vue 3 (Composition API), TypeScript (strict mode),
  Vite 6, Pinia, Tailwind CSS 4 (CSS-first config), Chart.js.
  Playwright MUST be configured as E2E test runner. Vitest MUST be
  configured as unit test runner. No additional UI frameworks without
  explicit justification.
- **Firmware**: Arduino C++ targeting ESP32 DevKit V1. Libraries
  MUST be installable via Arduino Library Manager.
- **Database**: Flyway manages all schema changes. Every migration
  file MUST follow the `V{n}__description.sql` naming convention.
  No manual DDL outside Flyway.
- **Monorepo**: `apps/backend`, `apps/frontend`, `apps/esp32` are
  independently built and deployed. Cross-app imports are prohibited.
- **Error Tracking**: Sentry MUST be configured in both backend
  (`sentry-spring-boot-starter`) and frontend (`@sentry/vue`).

## Development Workflow

- Features and specs MUST be tracked as GitHub Issues. Each issue
  MUST use labels for categorization (e.g., `feature`, `bug`,
  `epic`, `spec`). Local `specs/` markdown files MUST NOT be used
  for tracking active work — GitHub Issues are the single source
  of truth for planning and progress.
- `docs/USER_STORIES_V0.md` remains as a reference document for
  the original v0 user stories and acceptance criteria.
- Each implementation MUST reference the GitHub Issue number in its
  commit messages and PR description (e.g., `feat: add X (#42)`).
- Commits MUST be atomic and scoped to a single logical change.
  Commit messages MUST use conventional format (`feat:`, `fix:`,
  `chore:`, `docs:`) with the issue number appended.
- Environment-specific configuration MUST use `application.yml`
  profiles — never hardcoded values.
- The AlertScheduler runs every 15 minutes. Alert suppression windows
  (4 hours per plant per alert type) MUST be respected to avoid
  notification fatigue.
- **PR checklist** (every pull request MUST satisfy):
  1. Backend unit tests pass (`./mvnw test`).
  2. Frontend unit tests pass (`npm run test`).
  3. Playwright E2E tests pass (`npx playwright test`).
  4. No lint errors (`npm run lint`, no compiler warnings).
  5. Type check passes (`npm run type-check`).
  6. No architecture violations (controllers must not call repos).
  7. PR description references the GitHub Issue it resolves.

## Governance

This constitution is the authoritative reference for all architectural
and process decisions in FarmSense. When a proposed change conflicts
with a principle above, the constitution takes precedence unless
formally amended.

**Amendment procedure**:
1. Propose the change with rationale in a PR description.
2. Update this file with the new or modified principle.
3. Increment the version per semantic versioning (MAJOR for principle
   removal/redefinition, MINOR for additions, PATCH for clarifications).
4. Update the Sync Impact Report comment at the top of this file.
5. Verify dependent templates (plan, spec, tasks) still align.

**Compliance**: All code reviews MUST verify adherence to these
principles. Deviations MUST be documented in the PR with explicit
justification and a plan to converge back.

**Version**: 3.1.0 | **Ratified**: 2026-03-15 | **Last Amended**: 2026-03-15
