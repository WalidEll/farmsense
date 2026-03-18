-- ── Housing Location Enhancements (003) ──────────────────────────────
-- Add status and capacity fields to housing_locations

ALTER TABLE housing_locations
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'EMPTY',
    ADD COLUMN capacity INTEGER NOT NULL DEFAULT 0;

CREATE INDEX idx_housing_locations_status ON housing_locations(status);
