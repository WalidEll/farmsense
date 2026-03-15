# Implementation Plan: Breed Catalogue

**Branch**: `002-breed-catalogue` | **Date**: 2026-03-15 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `specs/002-breed-catalogue/spec.md`

## Summary

The Breed Catalogue introduces a structured, searchable library of poultry breed profiles that serves as both a reference tool and a template engine. When a farmer creates a flock and selects a breed, the system auto-generates feeding programs, vaccination schedules, production benchmarks, and housing guidelines. The catalogue ships pre-seeded with common Moroccan-market breeds and supports farmer-created custom entries.

**Technical approach**: Add a `breeds` table with child tables for feeding templates, vaccination schedules, benchmarks, and housing guidelines. Normalize the existing `flocks.breed` VARCHAR field to a `breed_id` FK. Build a full CRUD backend following the existing Controller → Service → Repository pattern. On the frontend, add a new catalogue section under `/poultry/breeds` with list, detail, comparison, and form views following existing Pinia store and Vue component patterns.

## Technical Context

**Language/Version**: Java 21 LTS (backend), TypeScript strict (frontend)
**Primary Dependencies**: Spring Boot 3.4.x, Vue 3 (Composition API), Pinia, Tailwind CSS 4
**Storage**: PostgreSQL 15 via Flyway migrations, Redis 7 (auth only)
**Testing**: JUnit 5 + Mockito (backend unit), @SpringBootTest (integration), Vitest (frontend unit), Playwright (E2E)
**Target Platform**: Web PWA (mobile-first, offline-capable)
**Project Type**: Web application (monorepo: `apps/backend` + `apps/frontend`)
**Performance Goals**: Catalogue search results within 1 second for up to 500 breeds
**Constraints**: Offline-capable (Workbox cache for catalogue reads), multilingual AR/FR/EN with RTL
**Scale/Scope**: ~15 pre-seeded breeds at launch, extensible to 500+

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Offline-First | PASS | Catalogue data cached via Workbox; custom breed creation queued offline via PouchDB |
| II. Multilingual & RTL | PASS | Breed names stored in FR/AR/EN; all new UI strings added with i18n keys |
| III. Clean Layered Architecture | PASS | Controller → Service → Repository for breeds; Store → Service for frontend |
| IV. API-Contract First | PASS | REST contracts defined in `contracts/` before implementation |
| V. Security by Default | PASS | JWT auth required; user-scoped custom breeds; validation on all DTOs |
| VI. Playwright E2E | PASS | E2E tests for catalogue browse, detail view, breed selection in flock creation |
| VII. Test Coverage | PASS | Unit tests for service logic; integration tests for endpoints; Vitest for stores |
| VIII. Records Mandate | PASS | All DTOs as Java records with `from()` factory methods |
| IX. CSS-First Rule | PASS | Tailwind 4 classes only, no JS config |
| X. Concurrency Standard | PASS | Virtual threads for all endpoints; no WebFlux needed |
| XI. Bundler Integrity | PASS | No custom plugins required |

**Gate result**: ALL PASS — proceed to Phase 0.

## Project Structure

### Documentation (this feature)

```text
specs/002-breed-catalogue/
├── spec.md
├── plan.md
├── research.md
├── data-model.md
├── contracts/
│   └── breed-catalogue-api.md
└── checklists/
    └── requirements.md
```

### Source Code (repository root)

```text
apps/backend/src/main/java/ma/farmsense/
├── entity/
│   ├── Breed.java
│   ├── BreedCategory.java
│   ├── BreedPurpose.java          # Extend existing FlockPurpose → LAYERS, BROILERS, DUAL_PURPOSE
│   ├── FeedingProgramTemplate.java
│   ├── VaccinationTemplate.java
│   ├── ProductionBenchmark.java
│   └── HousingGuideline.java
├── dto/poultry/
│   ├── CreateBreedRequest.java
│   ├── UpdateBreedRequest.java
│   ├── BreedSummaryResponse.java   # For catalogue listing (lightweight)
│   ├── BreedDetailResponse.java    # For full profile (includes child data)
│   ├── FeedingProgramTemplateDto.java
│   ├── VaccinationTemplateDto.java
│   ├── ProductionBenchmarkDto.java
│   └── HousingGuidelineDto.java
├── repository/
│   └── BreedRepository.java
├── service/
│   └── BreedService.java
├── controller/
│   └── BreedController.java
└── ...

apps/backend/src/main/resources/db/migration/
└── V16__breed_catalogue.sql

apps/frontend/src/
├── stores/
│   └── breeds.store.ts
├── types/
│   └── index.ts                    # Add Breed types
├── components/poultry/
│   ├── BreedCard.vue
│   ├── BreedForm.vue
│   ├── BreedCompareView.vue        # Inline comparison component
│   └── BreedTemplateSection.vue    # Reusable section for feeding/vacc/benchmark/housing
├── views/poultry/
│   ├── BreedsView.vue              # Catalogue listing with search/filter
│   └── BreedDetailView.vue         # Full breed profile
├── i18n/
│   ├── fr.ts                       # Add breed_* keys
│   ├── ar.ts
│   └── en.ts
└── router/
    └── index.ts                    # Add /poultry/breeds routes

apps/frontend/e2e/
└── breeds.spec.ts
```

**Structure Decision**: Follows existing monorepo pattern. Backend entities and DTOs go in `entity/` and `dto/poultry/` packages. Frontend follows the established `stores/`, `components/poultry/`, `views/poultry/` structure. A new `BreedPurpose` enum extends the existing `FlockPurpose` to add `DUAL_PURPOSE`.

## Complexity Tracking

No constitution violations. No complexity justification needed.
