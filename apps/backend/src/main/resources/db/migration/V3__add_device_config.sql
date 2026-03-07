-- Add device configuration columns
ALTER TABLE devices 
ADD COLUMN read_interval_ms INT DEFAULT 900000,
ADD COLUMN soil_dry_value INT DEFAULT 1024,
ADD COLUMN soil_wet_value INT DEFAULT 300;
