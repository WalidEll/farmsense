-- V18: Migrate existing flock breed text to breed_id references
-- Feature: 002-breed-catalogue

-- Match existing flock.breed text values to breed records by name (case-insensitive)
UPDATE flocks f
SET breed_id = b.id
FROM breeds b
WHERE LOWER(f.breed) = LOWER(b.name)
  AND f.breed IS NOT NULL;

-- Set flock.breed to NULL after migration (keeping column for reference but unused)
ALTER TABLE flocks ALTER COLUMN breed DROP NOT NULL;

-- Note: We keep the 'breed' column for now but it will be dropped in a future migration
-- after all data has been migrated and applications have been updated
