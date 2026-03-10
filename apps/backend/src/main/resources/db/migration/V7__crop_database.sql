CREATE TABLE crops (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    name_ar     VARCHAR(255),
    name_darija VARCHAR(255),
    scientific_name VARCHAR(255),
    category    VARCHAR(50) NOT NULL,
    description TEXT,
    description_ar TEXT,
    description_darija TEXT,
    image_url   VARCHAR(500),
    growing_season VARCHAR(100),
    days_to_harvest INTEGER,
    difficulty  VARCHAR(20),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE crop_requirements (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    crop_id         UUID NOT NULL UNIQUE REFERENCES crops(id) ON DELETE CASCADE,
    soil_moisture_min INTEGER,
    soil_moisture_max INTEGER,
    temp_min        DOUBLE PRECISION,
    temp_max        DOUBLE PRECISION,
    light_min       INTEGER,
    light_max       INTEGER,
    humidity_min    DOUBLE PRECISION,
    humidity_max    DOUBLE PRECISION,
    soil_type       VARCHAR(100),
    ph_min          DOUBLE PRECISION,
    ph_max          DOUBLE PRECISION,
    water_frequency VARCHAR(100)
);

CREATE TABLE crop_growth_stages (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    crop_id         UUID NOT NULL REFERENCES crops(id) ON DELETE CASCADE,
    stage_order     INTEGER NOT NULL,
    name            VARCHAR(100) NOT NULL,
    name_ar         VARCHAR(100),
    name_darija     VARCHAR(100),
    duration_days   INTEGER,
    description     TEXT,
    description_ar  TEXT,
    description_darija TEXT,
    UNIQUE (crop_id, stage_order)
);

CREATE TABLE crop_nutrients (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    crop_id         UUID NOT NULL UNIQUE REFERENCES crops(id) ON DELETE CASCADE,
    nitrogen_need   VARCHAR(20),
    phosphorus_need VARCHAR(20),
    potassium_need  VARCHAR(20),
    fertilizer_type VARCHAR(200),
    fertilizer_type_ar VARCHAR(200),
    fertilizer_type_darija VARCHAR(200),
    application_frequency VARCHAR(100)
);

CREATE TABLE crop_issues (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    crop_id         UUID NOT NULL REFERENCES crops(id) ON DELETE CASCADE,
    issue_type      VARCHAR(20) NOT NULL,
    name            VARCHAR(200) NOT NULL,
    name_ar         VARCHAR(200),
    name_darija     VARCHAR(200),
    symptoms        TEXT,
    symptoms_ar     TEXT,
    symptoms_darija TEXT,
    treatment       TEXT,
    treatment_ar    TEXT,
    treatment_darija TEXT,
    prevention      TEXT
);

CREATE INDEX idx_crops_category ON crops(category);
CREATE INDEX idx_crop_issues_crop ON crop_issues(crop_id);
CREATE INDEX idx_crop_growth_stages_crop ON crop_growth_stages(crop_id);
