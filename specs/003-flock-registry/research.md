# Research: Flock Registry (003)

**Phase**: 0 — Pre-design gap analysis and technical decisions
**Date**: 2026-03-16

---

## 1. Existing Codebase Gap Analysis

### What already exists

| Concern | Status | Notes |
|---------|--------|-------|
| `Flock` entity + JPA mapping | ✅ Exists | Has `birdCount`, `currentBirdCount`, `purpose` (LAYERS\|BROILERS), `status` (ACTIVE\|SOLD\|FINISHED), `startDate`, `name`, `nameAr`, `nameEn`, `breedId`, `supplierId`, `source`, `notes` |
| `FlockService` + `FlockController` | ✅ Exists | Full CRUD at `/api/v1/flocks`; soft-delete sets status to FINISHED |
| `FlockResponse` / DTOs | ✅ Exists | Java records |
| Breed catalogue | ✅ Exists | 15 seeded breeds; `BreedPurpose` already has DUAL_PURPOSE |
| `mortality_records` DB table | ✅ Table only | V11 migration created the table (count, mortality_date, cause, notes, flock_id) — **no Java entity, no service, no controller** |
| `poultry.store.ts` | ✅ Exists | Manages flocks, suppliers, customers |
| `FlockCard`, `FlockForm`, `FlocksView`, `FlockDetailView` | ✅ Exist | Functional; missing new fields |
| Housing / coop entity | ❌ Missing | `FarmLocation` exists for plants only (BED, POT, ROW…). No poultry-specific housing entity. |
| Batch code on Flock | ❌ Missing | No unique batch code column |
| DUAL_PURPOSE, BREEDERS in FlockPurpose | ❌ Missing | Current enum: LAYERS, BROILERS only |
| PHASED_OUT in FlockStatus | ❌ Missing | Current enum: ACTIVE, SOLD, FINISHED |
| Age-in-weeks calculation | ❌ Missing | `startDate` exists; no derived `ageWeeks` field returned by API or shown in UI |
| `type` column on `mortality_records` | ❌ Missing | Table has `cause` but not `type` (NATURAL_DEATH \| CULL) |
| Backend tests for poultry | ❌ Missing | All 9 existing test classes cover the plant module only |
| i18n keys for new fields | ❌ Missing | Missing: batchCode, housingLocation, ageWeeks, DUAL_PURPOSE, BREEDERS, PHASED_OUT, mortalityType, mortalityCause |

---

## 2. Technical Decisions

### Decision 1: Housing Location — separate entity, not reuse FarmLocation

**Decision**: Create a new `HousingLocation` entity with a COOP / PEN / FREE_RANGE type enum.

**Rationale**: `FarmLocation` serves the plant module (BED, POT, ROW, GREENHOUSE, FIELD, INDOOR, OTHER). Extending it with poultry-specific types would violate single-responsibility and create cross-domain coupling in reports and filters.

**Alternatives considered**:
- Reuse `FarmLocation` — rejected: wrong domain semantics, wrong type set, breaks plant-location filters.
- Free-text field on Flock — rejected: user explicitly chose managed list (Option A in spec clarification).

---

### Decision 2: PHASED_OUT vs. FINISHED

**Decision**: Add `PHASED_OUT` to the `FlockStatus` enum. Keep `FINISHED` as a legacy value. Update soft-delete logic to use `PHASED_OUT`. No data migration of existing rows needed (string column, no constraint enforcing exact values at DB level).

**Rationale**: Adding a new enum value is non-breaking. Renaming FINISHED would require a data migration. Existing records with `FINISHED` will continue to display correctly; the UI can label both FINISHED and PHASED_OUT the same way if desired.

---

### Decision 3: Batch code — user-scoped unique constraint

**Decision**: Add `batch_code VARCHAR(100) NOT NULL` to flocks with a `UNIQUE(user_id, batch_code)` composite unique constraint.

**Rationale**: Batch codes must be unique per operator but two operators can use the same code (e.g., both use "FL-2026-001"). The existing `name` field is not constrained to be unique, so a new dedicated column is needed.

---

### Decision 4: `mortality_records` — add `type` column via migration

**Decision**: Add `type VARCHAR(20) NOT NULL DEFAULT 'NATURAL_DEATH'` to `mortality_records` via a new migration (V19). Create Java `MortalityRecord` entity, `MortalityService`, and `MortalityController`.

**Rationale**: The table exists but has no Java layer. Adding the type column before creating the entity avoids a second migration. DEFAULT 'NATURAL_DEATH' keeps existing (empty) rows valid.

---

### Decision 5: Cause — enum, not free-text

**Decision**: Represent cause as a Java enum `MortalityCause`: `DISEASE`, `INJURY`, `PREDATOR`, `LOW_WEIGHT`, `HEAT_STRESS`, `UNKNOWN`, `OTHER`. Store as VARCHAR in DB.

**Rationale**: Standard enum enables filtering and future analytics (e.g., "show all disease-related losses"). Free-text would produce inconsistent data. The list covers the most common commercial poultry causes; `OTHER` + a notes field handles the tail.

---

### Decision 6: Age calculation — derived field in FlockResponse

**Decision**: Compute `ageWeeks` in `FlockService` from `startDate` to today and include it in `FlockResponse`. No DB column needed. Frontend reads it directly from the API response.

**Rationale**: Age is always relative to the current date. Storing it would require constant updates. A derived field is simpler and stays accurate automatically.

---

### Decision 7: Offline queue scope

**Decision**: Queue flock creation and mortality event creation via PouchDB (offline queue) on the frontend. Housing location mutations are low-frequency (infrequently created) and can fail gracefully without queuing.

**Rationale**: Constitution Principle I mandates offline-first for all user-facing write operations. Flock registration and mortality logging are the highest-frequency writes in this feature.

---

## 3. Migration Strategy

One new migration handles all schema changes together:

```
V19__flock_registry_enhancements.sql
```

Changes:
1. Create `housing_locations` table
2. Add `housing_location_id` FK to `flocks`
3. Add `batch_code VARCHAR(100) NOT NULL DEFAULT ''` to `flocks`
4. Add `UNIQUE(user_id, batch_code)` index (partial: WHERE batch_code <> '') — allows empty during migration
5. Add `type VARCHAR(20) NOT NULL DEFAULT 'NATURAL_DEATH'` to `mortality_records`

Highest existing migration: **V18**. Next: **V19**.

---

## 4. Scope Boundary

**In scope for this feature:**
- Housing Location CRUD (create, list, edit, delete)
- Flock: batch code field, expanded enums, age calculation, housing assignment
- Mortality/Cull events: create, list, headcount auto-update
- All required i18n keys (FR / AR / Darija)
- Backend unit tests for new services
- Playwright E2E coverage for critical paths

**Out of scope (deferred):**
- Headcount additions/restocking (spec assumption: no restocking in v0)
- Customer linkage on Sold status
- Mortality analytics / trend charts
- Housing location capacity limits
