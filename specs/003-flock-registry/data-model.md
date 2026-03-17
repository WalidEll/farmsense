# Data Model: Flock Registry (003)

**Date**: 2026-03-16
**Migration file**: `V19__flock_registry_enhancements.sql`

---

## Entity Changes

### Modified: `Flock`

| Field | Change | Details |
|-------|--------|---------|
| `batchCode` | **ADD** | `VARCHAR(100) NOT NULL`; unique per user (composite unique index on `user_id, batch_code` where batch_code <> '') |
| `housingLocation` | **ADD** | ManyToOne → `HousingLocation`, nullable, lazy |
| `purpose` enum | **EXTEND** | Add `DUAL_PURPOSE`, `BREEDERS` to `FlockPurpose` |
| `status` enum | **EXTEND** | Add `PHASED_OUT` to `FlockStatus` (keep `FINISHED` for legacy rows) |

No fields removed. No existing columns altered.

**Updated soft-delete behaviour**: `delete()` now sets status to `PHASED_OUT` (was `FINISHED`).

---

### New Entity: `HousingLocation`

**Table**: `housing_locations`

| Column | Type | Constraints |
|--------|------|-------------|
| `id` | UUID | PK, default gen_random_uuid() |
| `user_id` | UUID | FK → users(id) ON DELETE CASCADE, NOT NULL |
| `name` | VARCHAR(255) | NOT NULL |
| `name_ar` | VARCHAR(255) | nullable |
| `name_en` | VARCHAR(255) | nullable |
| `location_type` | VARCHAR(30) | NOT NULL — `COOP`, `PEN`, `FREE_RANGE` |
| `notes` | TEXT | nullable |
| `created_at` | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |
| `updated_at` | TIMESTAMPTZ | NOT NULL DEFAULT NOW() |

Indexes: `(user_id)`, `(user_id, location_type)`.

**Java entity fields:**

```
id: UUID
user: User (ManyToOne, lazy, required)
name: String (required, max 255)
nameAr: String (nullable)
nameEn: String (nullable)
locationType: HousingLocationType (required) — COOP | PEN | FREE_RANGE
notes: String (nullable, TEXT)
createdAt: Instant (not updatable)
updatedAt: Instant (auto-updated via @PreUpdate)
```

**Relationship**: One `HousingLocation` can have many `Flock` records (OneToMany from housing side; ManyToOne from flock side). No cascade delete — deleting a housing location with active flocks is blocked (service-level check).

---

### Modified: `mortality_records`

| Column | Change | Details |
|--------|--------|---------|
| `type` | **ADD** | `VARCHAR(20) NOT NULL DEFAULT 'NATURAL_DEATH'` — values: `NATURAL_DEATH`, `CULL` |

---

### New Entity (mapped from existing table): `MortalityRecord`

**Table**: `mortality_records` (already exists from V11)

| Column | Type | Notes |
|--------|------|-------|
| `id` | UUID | PK |
| `flock_id` | UUID | FK → flocks(id) ON DELETE CASCADE |
| `type` | VARCHAR(20) | `NATURAL_DEATH` \| `CULL` — **NEW in V19** |
| `count` | INTEGER | NOT NULL, must be ≥ 1 |
| `mortality_date` | DATE | NOT NULL |
| `cause` | VARCHAR(255) | nullable — standard values: `DISEASE`, `INJURY`, `PREDATOR`, `LOW_WEIGHT`, `HEAT_STRESS`, `UNKNOWN`, `OTHER` |
| `notes` | TEXT | nullable |
| `created_at` | TIMESTAMPTZ | NOT NULL |
| `updated_at` | TIMESTAMPTZ | NOT NULL |

**Java entity fields:**

```
id: UUID
flock: Flock (ManyToOne, lazy, required)
type: MortalityType (required) — NATURAL_DEATH | CULL
count: Integer (required, min=1)
mortalityDate: LocalDate (required)
cause: MortalityCause (nullable) — DISEASE | INJURY | PREDATOR | LOW_WEIGHT | HEAT_STRESS | UNKNOWN | OTHER
notes: String (nullable, TEXT)
createdAt: Instant (not updatable)
updatedAt: Instant (auto-updated)
```

**Side-effect on create**: `MortalityService.create()` decrements `flock.currentBirdCount` by `event.count` within the same transaction. Validates that `event.count ≤ flock.currentBirdCount` before persisting.

---

## New Enums

```java
// New
enum HousingLocationType { COOP, PEN, FREE_RANGE }

// New
enum MortalityType { NATURAL_DEATH, CULL }

// New
enum MortalityCause { DISEASE, INJURY, PREDATOR, LOW_WEIGHT, HEAT_STRESS, UNKNOWN, OTHER }

// Extended (existing enum)
enum FlockPurpose { LAYERS, BROILERS, DUAL_PURPOSE, BREEDERS }

// Extended (existing enum)
enum FlockStatus { ACTIVE, SOLD, PHASED_OUT, FINISHED }  // FINISHED = legacy
```

---

## Derived Fields

### `FlockResponse.ageWeeks`

- **Type**: `Integer` (nullable — null when `startDate` is null)
- **Calculation**: `ChronoUnit.WEEKS.between(startDate, LocalDate.now())`
- **Computed in**: `FlockResponse.from(Flock f)` factory method

---

## Entity Relationship Diagram (text)

```
User ──< HousingLocation
User ──< Flock >── Breed
           |
           └──> HousingLocation (nullable)
           |
           └──< MortalityRecord
```

**Note on `Breed` entity**: The `Breed` entity is an existing component implemented in the Breed Catalogue feature (002). It represents bird breeds (system-wide or user-defined).
- **Relationship**: `Flock` has a `ManyToOne` relationship with `Breed` (a flock belongs to one breed; a breed can have multiple flocks). This relationship is optional (`nullable = true`) at the database level but typically assigned during flock registration.
- **Ownership**: Both `Flock` and `Breed` are scoped to a `User` (though `Breed` can also be a system-wide record with `is_system = true`).

---

## Frontend Type Changes

**`/apps/frontend/src/types/index.ts`**

```typescript
// Extended
type FlockPurpose = 'LAYERS' | 'BROILERS' | 'DUAL_PURPOSE' | 'BREEDERS'
type FlockStatus  = 'ACTIVE' | 'SOLD' | 'PHASED_OUT' | 'FINISHED'

// Extended Flock interface
interface Flock {
  // existing fields...
  batchCode: string           // ADD
  housingLocationId?: string  // ADD
  housingLocationName?: string // ADD
  ageWeeks?: number           // ADD (derived, from API)
}

// New types
type HousingLocationType = 'COOP' | 'PEN' | 'FREE_RANGE'

interface HousingLocation {
  id: string
  name: string
  nameAr?: string
  nameEn?: string
  locationType: HousingLocationType
  notes?: string
  currentFlockCount: number  // derived, returned by API
  createdAt: string
  updatedAt: string
}

type MortalityType  = 'NATURAL_DEATH' | 'CULL'
type MortalityCause = 'DISEASE' | 'INJURY' | 'PREDATOR' | 'LOW_WEIGHT' | 'HEAT_STRESS' | 'UNKNOWN' | 'OTHER'

interface MortalityEvent {
  id: string
  flockId: string
  type: MortalityType
  count: number
  mortalityDate: string  // ISO date
  cause?: MortalityCause
  notes?: string
  createdAt: string
}

interface CreateMortalityEventRequest {
  type: MortalityType
  count: number
  mortalityDate: string
  cause?: MortalityCause
  notes?: string
}

interface CreateHousingLocationRequest {
  name: string
  nameAr?: string
  nameEn?: string
  locationType: HousingLocationType
  notes?: string
}
```
