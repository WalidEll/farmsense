# Data Model: Breed Catalogue

**Feature**: 002-breed-catalogue | **Date**: 2026-03-15

## Entities

### Breed

The central entity representing a poultry breed profile.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK, auto-generated | Unique identifier |
| user_id | UUID | FK → users(id), nullable | NULL = system pre-seeded; non-null = custom breed owned by this user |
| name | VARCHAR(255) | NOT NULL | Breed name (French, default language) |
| name_ar | VARCHAR(255) | nullable | Breed name in Arabic |
| name_en | VARCHAR(255) | nullable | Breed name in English |
| category | VARCHAR(20) | NOT NULL | COMMERCIAL, HERITAGE, CUSTOM |
| purpose | VARCHAR(20) | NOT NULL | LAYERS, BROILERS, DUAL_PURPOSE |
| origin | VARCHAR(255) | nullable | Country/region of origin |
| description | TEXT | nullable | General breed description (French) |
| description_ar | TEXT | nullable | Description in Arabic |
| description_en | TEXT | nullable | Description in English |
| image_url | VARCHAR(500) | nullable | URL to breed image |
| climate_suitability | VARCHAR(100) | nullable | e.g., "Hot/Arid", "Temperate", "Adaptable" |
| avg_weight_male_kg | DECIMAL(5,2) | nullable | Average adult male weight in kg |
| avg_weight_female_kg | DECIMAL(5,2) | nullable | Average adult female weight in kg |
| is_system | BOOLEAN | NOT NULL, default TRUE | TRUE for pre-seeded, FALSE for farmer-created |
| created_at | TIMESTAMPTZ | NOT NULL, default NOW() | Creation timestamp |
| updated_at | TIMESTAMPTZ | NOT NULL, default NOW() | Last update timestamp |

**Indexes**: `idx_breeds_user` on user_id, `idx_breeds_category` on category, `idx_breeds_purpose` on purpose, trigram index on (name, name_ar, name_en, description, description_ar, description_en) for search.

**Relationships**:
- Has many FeedingProgramTemplate
- Has many VaccinationTemplate
- Has many ProductionBenchmark
- Has many HousingGuideline
- Has many Flock (via flock.breed_id)

---

### FeedingProgramTemplate

Recommended feeding plan entries for a breed, organized by growth stage.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK, auto-generated | Unique identifier |
| breed_id | UUID | FK → breeds(id) ON DELETE CASCADE, NOT NULL | Parent breed |
| growth_stage | VARCHAR(20) | NOT NULL | STARTER, GROWER, FINISHER |
| age_start_days | INTEGER | NOT NULL | Start of age range in days |
| age_end_days | INTEGER | NOT NULL | End of age range in days |
| feed_type | VARCHAR(255) | NOT NULL | Type of feed recommended |
| daily_quantity_grams | DECIMAL(7,2) | NOT NULL | Grams per bird per day |
| feeding_frequency | INTEGER | NOT NULL, default 2 | Times per day |
| notes | TEXT | nullable | Additional notes |
| sort_order | INTEGER | NOT NULL, default 0 | Display ordering |

**Index**: `idx_feeding_templates_breed` on breed_id.

---

### VaccinationTemplate

Recommended vaccination schedule entries for a breed.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK, auto-generated | Unique identifier |
| breed_id | UUID | FK → breeds(id) ON DELETE CASCADE, NOT NULL | Parent breed |
| vaccine_name | VARCHAR(255) | NOT NULL | Name of the vaccine |
| recommended_age_days | INTEGER | NOT NULL | Age in days when vaccine should be administered |
| dosage | VARCHAR(100) | nullable | Dosage information |
| administration_route | VARCHAR(50) | NOT NULL | ORAL, INJECTION, EYE_DROP, SPRAY, DRINKING_WATER |
| is_mandatory | BOOLEAN | NOT NULL, default TRUE | Whether this vaccination is essential |
| notes | TEXT | nullable | Additional notes |
| sort_order | INTEGER | NOT NULL, default 0 | Display ordering |

**Index**: `idx_vaccination_templates_breed` on breed_id.

---

### ProductionBenchmark

Expected performance metrics for a breed.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK, auto-generated | Unique identifier |
| breed_id | UUID | FK → breeds(id) ON DELETE CASCADE, NOT NULL | Parent breed |
| metric_type | VARCHAR(50) | NOT NULL | EGG_PRODUCTION_RATE, DAILY_WEIGHT_GAIN, FEED_CONVERSION_RATIO, MORTALITY_RATE, PEAK_PRODUCTION_AGE |
| expected_value | DECIMAL(10,4) | NOT NULL | Expected metric value |
| unit | VARCHAR(50) | NOT NULL | Unit of measurement (%, g/day, ratio, weeks) |
| age_start_days | INTEGER | nullable | Start of applicable age range |
| age_end_days | INTEGER | nullable | End of applicable age range |
| notes | TEXT | nullable | Additional context |
| sort_order | INTEGER | NOT NULL, default 0 | Display ordering |

**Index**: `idx_benchmarks_breed` on breed_id.

---

### HousingGuideline

Recommended housing parameters for a breed.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK, auto-generated | Unique identifier |
| breed_id | UUID | FK → breeds(id) ON DELETE CASCADE, NOT NULL | Parent breed |
| parameter_name | VARCHAR(100) | NOT NULL | e.g., SPACE_PER_BIRD, TEMPERATURE_RANGE, HUMIDITY_RANGE, VENTILATION_RATE, LIGHTING_SCHEDULE |
| recommended_value | VARCHAR(255) | NOT NULL | The recommended value (e.g., "0.1 m²", "18-24°C", "14h light / 10h dark") |
| unit | VARCHAR(50) | nullable | Unit of measurement |
| growth_stage | VARCHAR(20) | nullable | STARTER, GROWER, FINISHER, ALL (if stage-specific) |
| notes | TEXT | nullable | Additional guidance |
| sort_order | INTEGER | NOT NULL, default 0 | Display ordering |

**Index**: `idx_housing_guidelines_breed` on breed_id.

---

### Flock (Modified)

Add breed_id foreign key to existing flocks table.

| Field | Type | Change | Description |
|-------|------|--------|-------------|
| breed_id | UUID | ADD, FK → breeds(id) ON DELETE SET NULL, nullable | Reference to the breed catalogue entry |
| breed | VARCHAR(255) | DROP (after data migration) | Legacy free-text breed field |

**Migration strategy**:
1. `V16__breed_catalogue.sql`: Create breeds table and child tables; add `breed_id` column to flocks
2. `V17__seed_breeds.sql`: Insert pre-seeded breed data with template records
3. `V18__migrate_flock_breeds.sql`: Match existing `flock.breed` text values to breed records, set `breed_id`, then drop the `breed` column

---

## State Transitions

### Breed Lifecycle

```
[Created] → [Active] → [Deleted]
                ↑
          [Edited/Updated]
```

- Pre-seeded breeds are created during migration and cannot be deleted by users
- Custom breeds can be edited/deleted by their owner
- Deleting a custom breed that is referenced by flocks requires confirmation; flocks retain `breed_id = NULL` after deletion

### Flock-Breed Relationship

```
Flock created → Breed selected → Templates auto-generated → Programs editable
                     ↓
              Breed changed → Offer: regenerate or keep programs
```

## Validation Rules

- Breed `name` is required and unique per user scope (system breeds globally unique; custom breeds unique per user)
- `category` must be one of: COMMERCIAL, HERITAGE, CUSTOM
- `purpose` must be one of: LAYERS, BROILERS, DUAL_PURPOSE
- Feeding program `age_start_days` must be < `age_end_days`
- Vaccination `recommended_age_days` must be > 0
- Production benchmark `expected_value` must be > 0
- Custom breeds require `is_system = false` and a non-null `user_id`
- Pre-seeded breeds require `is_system = true` and `user_id = NULL`
