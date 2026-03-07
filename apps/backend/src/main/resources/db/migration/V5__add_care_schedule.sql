ALTER TABLE plants
ADD COLUMN watering_interval_days INT NOT NULL DEFAULT 7,
ADD COLUMN fertilising_interval_days INT NOT NULL DEFAULT 30,
ADD COLUMN repotting_interval_days INT NOT NULL DEFAULT 180,
ADD COLUMN last_watered_at TIMESTAMPTZ,
ADD COLUMN last_fertilised_at TIMESTAMPTZ,
ADD COLUMN last_repotted_at TIMESTAMPTZ;
