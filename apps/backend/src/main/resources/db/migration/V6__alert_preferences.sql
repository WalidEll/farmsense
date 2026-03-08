CREATE TABLE alert_preferences (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    soil_dry_enabled      BOOLEAN NOT NULL DEFAULT TRUE,
    soil_wet_enabled      BOOLEAN NOT NULL DEFAULT TRUE,
    temp_high_enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    temp_low_enabled      BOOLEAN NOT NULL DEFAULT TRUE,
    light_low_enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    device_offline_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    quiet_hours_start     INTEGER,
    quiet_hours_end       INTEGER,
    channel_whatsapp      BOOLEAN NOT NULL DEFAULT TRUE,
    channel_push          BOOLEAN NOT NULL DEFAULT TRUE
);
