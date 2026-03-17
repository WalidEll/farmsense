-- ── Flock Registry Enhancements (003) ────────────────────────────────
-- 1. Housing Locations: new managed entity for coops / pens / free-range zones
-- 2. Flocks: add batch_code (unique per user) and housing_location_id FK
-- 3. Mortality Records: add type column (NATURAL_DEATH | CULL)

-- ── 1. Housing Locations ────────────────────────────────────────────
CREATE TABLE housing_locations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    name_ar         VARCHAR(255),
    name_en         VARCHAR(255),
    location_type   VARCHAR(30) NOT NULL, -- COOP, PEN, FREE_RANGE
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_housing_locations_user ON housing_locations(user_id);
CREATE INDEX idx_housing_locations_user_type ON housing_locations(user_id, location_type);

-- ── 2. Flock Enhancements ───────────────────────────────────────────
ALTER TABLE flocks
    ADD COLUMN housing_location_id UUID REFERENCES housing_locations(id) ON DELETE SET NULL,
    ADD COLUMN batch_code VARCHAR(100) NOT NULL DEFAULT '';

-- Partial unique index: enforce uniqueness only for non-empty batch codes per user
-- This allows existing rows (which get DEFAULT '') to coexist, and future rows
-- with a real batch code must be unique within the same user.
CREATE UNIQUE INDEX uq_flock_batch_code
    ON flocks(user_id, batch_code)
    WHERE batch_code <> '';

CREATE INDEX idx_flocks_housing_location ON flocks(housing_location_id);

-- ── 3. Mortality Records: add type column ───────────────────────────
ALTER TABLE mortality_records
    ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'NATURAL_DEATH';

CREATE INDEX idx_mortality_records_type ON mortality_records(type);
