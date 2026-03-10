-- ── Crop Planning Module ──────────────────────────────────────────
-- Per-user seasonal planning: plans, locations, plantings, notes, tasks

CREATE TABLE crop_plans (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    name_ar     VARCHAR(255),
    name_en     VARCHAR(255),
    description TEXT,
    description_ar TEXT,
    description_en TEXT,
    season      VARCHAR(20) NOT NULL,   -- SPRING, SUMMER, AUTUMN, WINTER, YEAR_ROUND
    year        INTEGER NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'DRAFT',  -- DRAFT, ACTIVE, COMPLETED, ARCHIVED
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE farm_locations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    name_ar     VARCHAR(255),
    name_en     VARCHAR(255),
    description TEXT,
    description_ar TEXT,
    description_en TEXT,
    location_type VARCHAR(20) NOT NULL,  -- BED, POT, ROW, GREENHOUSE, FIELD, INDOOR, OTHER
    area_m2     DOUBLE PRECISION,
    latitude    DOUBLE PRECISION,
    longitude   DOUBLE PRECISION,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE plantings (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    crop_plan_id            UUID NOT NULL REFERENCES crop_plans(id) ON DELETE CASCADE,
    crop_id                 UUID NOT NULL REFERENCES crops(id),
    farm_location_id        UUID REFERENCES farm_locations(id) ON DELETE SET NULL,
    status                  VARCHAR(20) NOT NULL DEFAULT 'PLANNED',
    -- PLANNED, SOWN, TRANSPLANTED, GROWING, HARVESTING, COMPLETED, FAILED
    quantity                INTEGER,
    area_m2                 DOUBLE PRECISION,
    notes                   TEXT,
    -- Planned dates
    planned_sow_date        DATE,
    planned_transplant_date DATE,
    planned_harvest_date    DATE,
    -- Actual dates (filled on status change)
    actual_sow_date         DATE,
    actual_transplant_date  DATE,
    actual_harvest_date     DATE,
    -- Yield
    yield_amount            DOUBLE PRECISION,
    yield_unit              VARCHAR(20),  -- KG, UNITS, BUNCHES, LITERS
    quality_rating          INTEGER,      -- 1-5
    harvest_notes           TEXT,
    failure_reason          TEXT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE planting_notes (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    planting_id     UUID NOT NULL REFERENCES plantings(id) ON DELETE CASCADE,
    content         TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE planting_tasks (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    planting_id     UUID NOT NULL REFERENCES plantings(id) ON DELETE CASCADE,
    task_type       VARCHAR(20) NOT NULL,  -- SOW, TRANSPLANT, WATER, FERTILIZE, PRUNE, HARVEST, PEST_CHECK
    title           VARCHAR(255) NOT NULL,
    title_ar        VARCHAR(255),
    title_en        VARCHAR(255),
    due_date        DATE NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, DONE, SKIPPED
    completed_at    TIMESTAMPTZ,
    skip_reason     VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_crop_plans_user ON crop_plans(user_id);
CREATE INDEX idx_crop_plans_status ON crop_plans(status);
CREATE INDEX idx_farm_locations_user ON farm_locations(user_id);
CREATE INDEX idx_plantings_plan ON plantings(crop_plan_id);
CREATE INDEX idx_plantings_crop ON plantings(crop_id);
CREATE INDEX idx_plantings_location ON plantings(farm_location_id);
CREATE INDEX idx_plantings_status ON plantings(status);
CREATE INDEX idx_planting_notes_planting ON planting_notes(planting_id);
CREATE INDEX idx_planting_tasks_planting ON planting_tasks(planting_id);
CREATE INDEX idx_planting_tasks_due_date ON planting_tasks(due_date);
CREATE INDEX idx_planting_tasks_status ON planting_tasks(status);
