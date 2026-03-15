# Research: Breed Catalogue

**Feature**: 002-breed-catalogue | **Date**: 2026-03-15

## R1: Breed Data Normalization Strategy

**Decision**: Add a `breeds` table with a FK from `flocks.breed_id`, and migrate existing free-text `flocks.breed` data to breed records.

**Rationale**: The current `breed` column is a free-text VARCHAR(255) with no validation. Normalizing to a FK enables the template engine functionality (auto-generating programs from breed profiles), ensures data consistency, and allows structured search/filter. The migration will create breed records for any distinct existing `breed` values and link them.

**Alternatives considered**:
- Keep free-text breed field alongside a separate catalogue table — rejected because it creates two sources of truth and complicates the template engine lookup.
- Use a JSON column for breed data — rejected because it prevents relational queries and violates the existing entity pattern.

## R2: Pre-Seeded Breed Data Source

**Decision**: Use a Flyway seed migration (`V17__seed_breeds.sql`) to populate the initial 15+ breed profiles with template data. Pre-seeded breeds have `user_id = NULL` to distinguish them from custom breeds.

**Rationale**: Flyway migrations are the established pattern for data seeding in this project (see `V8__seed_crops.sql`). NULL user_id is a clean ownership signal — pre-seeded breeds are system-owned and visible to all users; custom breeds have a user_id and are scoped to that user.

**Alternatives considered**:
- Application-level seeding via `CommandLineRunner` — rejected because it bypasses the migration audit trail and can cause duplicate seeding issues.
- External CSV/JSON import — rejected because it adds complexity and is harder to version control.

## R3: Breed Purpose Enum

**Decision**: Create a new `BreedPurpose` enum with values `LAYERS`, `BROILERS`, `DUAL_PURPOSE`. This is separate from `FlockPurpose` (which remains `LAYERS`, `BROILERS` only, since a flock has a concrete purpose, not "dual").

**Rationale**: A breed can be dual-purpose (suitable for both eggs and meat), but a specific flock always has a single operational purpose. Keeping separate enums avoids confusion and preserves the existing flock semantics.

**Alternatives considered**:
- Extend `FlockPurpose` to include `DUAL_PURPOSE` — rejected because it changes flock semantics and affects existing flock filtering logic.
- Use a tags/labels approach for breed capabilities — over-engineered for 3 fixed values.

## R4: Template Data Structure

**Decision**: Use separate child tables (`feeding_program_templates`, `vaccination_templates`, `production_benchmarks`, `housing_guidelines`) with FK to `breeds.id`, rather than JSON columns.

**Rationale**: Separate tables allow individual records to be queried, filtered, and updated independently. They align with the existing relational entity pattern in the project and enable future features like "find breeds with Newcastle vaccination" or "compare feeding costs across breeds."

**Alternatives considered**:
- JSONB columns on the breeds table — rejected because it prevents relational queries and makes partial updates harder.
- A single generic `breed_templates` table with a `type` discriminator — rejected because the four template types have distinct schemas.

## R5: Comparison Feature Scope

**Decision**: Implement comparison as a client-side feature that loads full profiles for 2-3 selected breeds and renders them in aligned columns. No dedicated comparison endpoint needed.

**Rationale**: The breed detail endpoint already returns all profile data. Client-side comparison avoids a specialized backend endpoint and keeps the implementation simple. With a maximum of 3 breeds and relatively small profile data, loading 3 full profiles is efficient.

**Alternatives considered**:
- Dedicated `/breeds/compare?ids=a,b,c` endpoint — unnecessary complexity since the data is already available from individual detail calls.

## R6: Flock-Breed Integration

**Decision**: Add `breed_id UUID REFERENCES breeds(id) ON DELETE SET NULL` to the `flocks` table. Keep the old `breed` VARCHAR column temporarily for data migration, then drop it in a subsequent migration.

**Rationale**: `ON DELETE SET NULL` preserves flock records even if a custom breed is deleted (matching spec requirement FR-010). A two-step migration (add FK → migrate data → drop old column) is safer than a single-step migration.

**Alternatives considered**:
- `ON DELETE RESTRICT` — rejected because it would prevent breed deletion entirely, conflicting with FR-009.
- `ON DELETE CASCADE` — rejected because deleting a breed should not delete flocks.

## R7: Search Implementation

**Decision**: Use PostgreSQL `ILIKE` with trigram index (`pg_trgm`) for full-text search across breed names and descriptions in all languages.

**Rationale**: The catalogue will have at most hundreds of breeds, making `ILIKE` with trigram indexing sufficient and simpler than full-text search vectors. Trigram indexes support partial matching well, which is important for incremental search-as-you-type behavior.

**Alternatives considered**:
- PostgreSQL `tsvector`/`tsquery` full-text search — over-engineered for the data volume and doesn't handle partial matches as naturally.
- Application-level filtering — rejected because it loads all breeds on every search.

## R8: Offline Catalogue Access

**Decision**: Workbox caches GET responses for `/api/v1/breeds` (list) and `/api/v1/breeds/:id` (detail) endpoints. Custom breed creation is queued via PouchDB for replay on reconnect.

**Rationale**: Follows the existing offline-first pattern established in the PWA. Catalogue data is relatively static and small, making it ideal for cache-first strategies.

**Alternatives considered**:
- IndexedDB full catalogue sync — over-engineered for the data volume.
- No offline support — violates Constitution Principle I.
