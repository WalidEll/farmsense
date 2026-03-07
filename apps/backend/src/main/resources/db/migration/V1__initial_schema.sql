-- V1__initial_schema.sql
-- FarmSense v0 database schema
-- Managed by Flyway — do NOT edit manually after first migration

-- ── Extensions ───────────────────────────────────────────────
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ── Users ────────────────────────────────────────────────────
CREATE TABLE users (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email          VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    name           VARCHAR(100) NOT NULL,
    phone_wa       VARCHAR(20),                          -- WhatsApp number
    lang           VARCHAR(10) NOT NULL DEFAULT 'FR',    -- AR / FR / DARIJA
    tier           VARCHAR(20) NOT NULL DEFAULT 'FREE',  -- FREE / HOME_PRO
    claim_token    VARCHAR(10),                          -- one-time device setup code
    claim_token_expires_at TIMESTAMPTZ,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Plants ───────────────────────────────────────────────────
CREATE TABLE plants (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name           VARCHAR(100) NOT NULL,
    species        VARCHAR(100),
    location       VARCHAR(50),                          -- INDOOR / OUTDOOR / BALCONY
    photo_url      VARCHAR(500),
    soil_min       INT NOT NULL DEFAULT 30,              -- %
    soil_max       INT NOT NULL DEFAULT 80,              -- %
    temp_min       DECIMAL(5,2) NOT NULL DEFAULT 15.0,   -- °C
    temp_max       DECIMAL(5,2) NOT NULL DEFAULT 30.0,   -- °C
    light_min      INT NOT NULL DEFAULT 500,             -- lux
    deleted_at     TIMESTAMPTZ,                          -- soft delete
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Devices (ESP32) ──────────────────────────────────────────
CREATE TABLE devices (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id      VARCHAR(20) NOT NULL UNIQUE,          -- e.g. FS-00042
    user_id        UUID REFERENCES users(id) ON DELETE SET NULL,
    plant_id       UUID REFERENCES plants(id) ON DELETE SET NULL,
    label          VARCHAR(100),                         -- user-defined name
    claimed_at     TIMESTAMPTZ,
    last_seen_at   TIMESTAMPTZ,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Sensor Readings ──────────────────────────────────────────
CREATE TABLE sensor_readings (
    id             BIGSERIAL PRIMARY KEY,
    device_id      VARCHAR(20) NOT NULL,
    plant_id       UUID REFERENCES plants(id) ON DELETE SET NULL,
    temperature    DECIMAL(5,2),                         -- °C
    humidity       DECIMAL(5,2),                         -- %
    soil_moisture  INT,                                  -- 0-100%
    light_lux      DECIMAL(10,2),                        -- lux
    source         VARCHAR(20) NOT NULL DEFAULT 'SENSOR', -- SENSOR / MANUAL
    recorded_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Index for chart queries
CREATE INDEX idx_readings_plant_recorded ON sensor_readings(plant_id, recorded_at DESC);
CREATE INDEX idx_readings_device ON sensor_readings(device_id, recorded_at DESC);

-- ── Alerts ───────────────────────────────────────────────────
CREATE TABLE alerts (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plant_id       UUID REFERENCES plants(id) ON DELETE SET NULL,
    type           VARCHAR(30) NOT NULL,  -- SOIL_DRY / SOIL_WET / TEMP_HIGH / TEMP_LOW / LIGHT_LOW
    severity       VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',  -- LOW / MEDIUM / HIGH
    sensor_value   DECIMAL(10,2),
    msg_darija     TEXT,
    msg_fr         TEXT,
    msg_ar         TEXT,
    wa_sent        BOOLEAN DEFAULT FALSE,
    push_sent      BOOLEAN DEFAULT FALSE,
    triggered_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ack_at         TIMESTAMPTZ
);

CREATE INDEX idx_alerts_user_triggered ON alerts(user_id, triggered_at DESC);
CREATE INDEX idx_alerts_plant ON alerts(plant_id, triggered_at DESC);

-- ── Diagnoses ────────────────────────────────────────────────
CREATE TABLE diagnoses (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plant_id       UUID REFERENCES plants(id) ON DELETE SET NULL,
    photo_url      VARCHAR(500) NOT NULL,
    problem_name   VARCHAR(200),
    severity       VARCHAR(20),  -- LOW / MEDIUM / HIGH
    treatment_fr   TEXT,
    treatment_ar   TEXT,
    treatment_darija TEXT,
    prevention     TEXT,
    raw_response   TEXT,         -- full Claude API response for debugging
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Care Log (manual task completion) ────────────────────────
CREATE TABLE care_log (
    id             UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    plant_id       UUID NOT NULL REFERENCES plants(id) ON DELETE CASCADE,
    task_type      VARCHAR(30) NOT NULL,  -- WATER / FERTILISE / REPOT
    done_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    notes          TEXT
);

CREATE INDEX idx_care_log_plant ON care_log(plant_id, done_at DESC);
