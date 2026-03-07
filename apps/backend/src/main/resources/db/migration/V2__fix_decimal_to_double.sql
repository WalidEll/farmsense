-- V2: Align DECIMAL columns with Hibernate's mapping for Java double/Double (DOUBLE PRECISION)
-- Hibernate maps Java double/Double → float8 (DOUBLE PRECISION), not NUMERIC.
-- Without this, schema-validation throws on startup.

ALTER TABLE plants
    ALTER COLUMN temp_min  TYPE DOUBLE PRECISION,
    ALTER COLUMN temp_max  TYPE DOUBLE PRECISION;

ALTER TABLE sensor_readings
    ALTER COLUMN temperature TYPE DOUBLE PRECISION,
    ALTER COLUMN humidity    TYPE DOUBLE PRECISION,
    ALTER COLUMN light_lux   TYPE DOUBLE PRECISION;

ALTER TABLE alerts
    ALTER COLUMN sensor_value TYPE DOUBLE PRECISION;
