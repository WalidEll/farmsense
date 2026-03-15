-- V16: Breed Catalogue - Core tables
-- Feature: 002-breed-catalogue

-- Enable pg_trgm extension for trigram indexes
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ── Breeds table ─────────────────────────────────────────────────────
CREATE TABLE breeds (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID REFERENCES users(id) ON DELETE SET NULL,
    name                VARCHAR(255) NOT NULL,
    name_ar             VARCHAR(255),
    name_en             VARCHAR(255),
    category            VARCHAR(20) NOT NULL, -- COMMERCIAL, HERITAGE, CUSTOM
    purpose             VARCHAR(20) NOT NULL, -- LAYERS, BROILERS, DUAL_PURPOSE
    origin              VARCHAR(255),
    description         TEXT,
    description_ar      TEXT,
    description_en      TEXT,
    image_url           VARCHAR(500),
    climate_suitability VARCHAR(100),
    avg_weight_male_kg  DECIMAL(5,2),
    avg_weight_female_kg DECIMAL(5,2),
    is_system           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Feeding Program Templates table ─────────────────────────────────
CREATE TABLE feeding_program_templates (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    breed_id            UUID NOT NULL REFERENCES breeds(id) ON DELETE CASCADE,
    growth_stage       VARCHAR(20) NOT NULL, -- STARTER, GROWER, FINISHER
    age_start_days      INTEGER NOT NULL,
    age_end_days        INTEGER NOT NULL,
    feed_type           VARCHAR(255) NOT NULL,
    daily_quantity_grams DECIMAL(7,2) NOT NULL,
    feeding_frequency   INTEGER NOT NULL DEFAULT 2,
    notes               TEXT,
    sort_order          INTEGER NOT NULL DEFAULT 0
);

-- ── Vaccination Templates table ─────────────────────────────────────
CREATE TABLE vaccination_templates (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    breed_id            UUID NOT NULL REFERENCES breeds(id) ON DELETE CASCADE,
    vaccine_name        VARCHAR(255) NOT NULL,
    recommended_age_days INTEGER NOT NULL,
    dosage              VARCHAR(100),
    administration_route VARCHAR(50) NOT NULL, -- ORAL, INJECTION, EYE_DROP, SPRAY, DRINKING_WATER
    is_mandatory        BOOLEAN NOT NULL DEFAULT TRUE,
    notes               TEXT,
    sort_order          INTEGER NOT NULL DEFAULT 0
);

-- ── Production Benchmarks table ─────────────────────────────────────
CREATE TABLE production_benchmarks (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    breed_id            UUID NOT NULL REFERENCES breeds(id) ON DELETE CASCADE,
    metric_type         VARCHAR(50) NOT NULL, -- EGG_PRODUCTION_RATE, DAILY_WEIGHT_GAIN, FEED_CONVERSION_RATIO, MORTALITY_RATE, PEAK_PRODUCTION_AGE
    expected_value      DECIMAL(10,4) NOT NULL,
    unit                VARCHAR(50) NOT NULL,
    age_start_days      INTEGER,
    age_end_days        INTEGER,
    notes               TEXT,
    sort_order          INTEGER NOT NULL DEFAULT 0
);

-- ── Housing Guidelines table ─────────────────────────────────────────
CREATE TABLE housing_guidelines (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    breed_id            UUID NOT NULL REFERENCES breeds(id) ON DELETE CASCADE,
    parameter_name      VARCHAR(100) NOT NULL, -- SPACE_PER_BIRD, TEMPERATURE_RANGE, HUMIDITY_RANGE, VENTILATION_RATE, LIGHTING_SCHEDULE
    recommended_value   VARCHAR(255) NOT NULL,
    unit                VARCHAR(50),
    growth_stage        VARCHAR(20), -- STARTER, GROWER, FINISHER, ALL
    notes               TEXT,
    sort_order          INTEGER NOT NULL DEFAULT 0
);

-- ── Add breed_id to flocks table ────────────────────────────────────
ALTER TABLE flocks ADD COLUMN breed_id UUID REFERENCES breeds(id) ON DELETE SET NULL;
CREATE INDEX idx_flocks_breed_id ON flocks(breed_id);

-- ── Indexes ──────────────────────────────────────────────────────────
CREATE INDEX idx_breeds_user ON breeds(user_id);
CREATE INDEX idx_breeds_category ON breeds(category);
CREATE INDEX idx_breeds_purpose ON breeds(purpose);
CREATE INDEX idx_breeds_name ON breeds(name);
CREATE INDEX idx_breeds_name_ar ON breeds(name_ar);
CREATE INDEX idx_breeds_name_en ON breeds(name_en);

-- Trigram indexes for full-text search
CREATE INDEX idx_breeds_name_trgm ON breeds USING gin(name gin_trgm_ops);
CREATE INDEX idx_breeds_name_ar_trgm ON breeds USING gin(name_ar gin_trgm_ops);
CREATE INDEX idx_breeds_name_en_trgm ON breeds USING gin(name_en gin_trgm_ops);
CREATE INDEX idx_breeds_description_trgm ON breeds USING gin(description gin_trgm_ops);
CREATE INDEX idx_breeds_description_ar_trgm ON breeds USING gin(description_ar gin_trgm_ops);
CREATE INDEX idx_breeds_description_en_trgm ON breeds USING gin(description_en gin_trgm_ops);

CREATE INDEX idx_feeding_templates_breed ON feeding_program_templates(breed_id);
CREATE INDEX idx_vaccination_templates_breed ON vaccination_templates(breed_id);
CREATE INDEX idx_benchmarks_breed ON production_benchmarks(breed_id);
CREATE INDEX idx_housing_guidelines_breed ON housing_guidelines(breed_id);

-- Update trigger function for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_breeds_updated_at BEFORE UPDATE ON breeds
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
