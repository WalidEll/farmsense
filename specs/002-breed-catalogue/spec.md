# Feature Specification: Breed Catalogue

**Feature Branch**: `002-breed-catalogue`
**Created**: 2026-03-15
**Status**: Draft
**Input**: User description: "The Breed Catalogue is a structured, searchable library of poultry breed profiles that serves two purposes: it acts as a standalone reference tool where farmers can browse and compare breeds, and it functions as the system's template engine — when a farmer creates a new flock and selects a breed, the catalogue auto-generates recommended feeding programs, vaccination schedules, production benchmarks, and housing guidelines, reducing setup time and embedding best practices into daily operations. The catalogue ships pre-seeded with the most common commercial and heritage breeds for the target market, and supports farmer-created custom entries for local crossbreeds or uncommon varieties."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Browse and Search Breed Catalogue (Priority: P1)

A farmer opens the breed catalogue to explore available poultry breeds. They see a list of breed cards showing key information at a glance (name, category, primary purpose). They can search by breed name, filter by category (commercial, heritage, custom), and filter by purpose (layers, broilers, dual-purpose). Results update as the farmer types or selects filters.

**Why this priority**: Browsing and searching is the foundation of the catalogue — without it, farmers cannot discover or access any breed information. Every other story depends on this capability.

**Independent Test**: Can be fully tested by navigating to the catalogue, verifying breed cards display, and confirming search and filter controls return correct results.

**Acceptance Scenarios**:

1. **Given** the catalogue contains pre-seeded breeds, **When** a farmer opens the catalogue page, **Then** they see a paginated list of breed cards with name, category, purpose, and a thumbnail image.
2. **Given** the farmer is viewing the catalogue, **When** they type "Sasso" in the search field, **Then** only breeds whose name contains "Sasso" are displayed.
3. **Given** the farmer is viewing the catalogue, **When** they select the "Layers" purpose filter, **Then** only breeds categorized for egg production are shown.
4. **Given** the farmer applies both a category filter and a search term, **When** results are returned, **Then** only breeds matching both criteria are displayed.
5. **Given** the farmer searches for a term with no matches, **When** results are empty, **Then** a helpful empty-state message is shown suggesting to broaden the search or create a custom breed.

---

### User Story 2 - View Breed Profile Details (Priority: P1)

A farmer selects a breed from the catalogue to view its full profile. The profile displays the breed's general information (origin, description, typical weight ranges, climate suitability) along with four structured sections: recommended feeding program by growth stage, vaccination schedule, production benchmarks (egg yield, growth rate, feed conversion ratio), and housing guidelines (space per bird, ventilation, temperature ranges).

**Why this priority**: The detailed profile is the core value of the catalogue as a reference tool. Farmers need this information to make informed decisions about which breeds to raise.

**Independent Test**: Can be tested by selecting any pre-seeded breed and verifying all profile sections display complete, accurate information.

**Acceptance Scenarios**:

1. **Given** the farmer is viewing the catalogue, **When** they select a breed card, **Then** the breed's full profile page opens showing general information and all four structured sections.
2. **Given** the farmer is viewing a breed profile, **When** they look at the feeding program section, **Then** they see feeding recommendations organized by growth stage (starter, grower, finisher) with feed type, daily quantity per bird, and feeding frequency.
3. **Given** the farmer is viewing a breed profile, **When** they look at the vaccination schedule, **Then** they see a chronological list of vaccinations with vaccine name, recommended age, dosage, and administration route.
4. **Given** the farmer is viewing a breed profile, **When** they look at production benchmarks, **Then** they see expected metrics (egg production rate for layers, weight gain timeline for broilers, feed conversion ratio) with age-specific targets.
5. **Given** the farmer is viewing a breed profile, **When** they look at housing guidelines, **Then** they see recommended space per bird, temperature ranges, ventilation requirements, and lighting schedules.

---

### User Story 3 - Auto-Generate Flock Programs from Breed Selection (Priority: P1)

When a farmer creates a new flock and selects a breed from the catalogue, the system automatically generates a recommended feeding program, vaccination schedule, production benchmarks, and housing guidelines for that flock based on the breed's profile. The farmer can review and adjust these generated recommendations before saving.

**Why this priority**: This is the primary workflow integration — it transforms the catalogue from a passive reference into an active productivity tool that saves farmers significant setup time and embeds best practices.

**Independent Test**: Can be tested by creating a new flock, selecting a breed, and verifying that all four program types are pre-populated with the breed's recommended values and are editable before saving.

**Acceptance Scenarios**:

1. **Given** a farmer is creating a new flock, **When** they select a breed from the catalogue, **Then** the system auto-populates feeding program, vaccination schedule, production benchmarks, and housing guidelines based on the breed's template data.
2. **Given** auto-generated programs are displayed, **When** the farmer reviews them, **Then** they can modify any value (quantities, dates, thresholds) before saving.
3. **Given** auto-generated programs are displayed, **When** the farmer saves the flock without changes, **Then** the breed's default recommendations are stored as the flock's active programs.
4. **Given** a farmer selects a custom breed that has incomplete template data, **When** programs are generated, **Then** only sections with available data are pre-populated, and empty sections are clearly marked for manual entry.
5. **Given** a farmer changes the breed selection on an existing flock, **When** they confirm the change, **Then** the system offers to regenerate programs from the new breed's template or keep the current programs.

---

### User Story 4 - Create Custom Breed Entry (Priority: P2)

A farmer creates a custom breed entry for a local crossbreed or uncommon variety not found in the pre-seeded catalogue. They provide the breed's name, category, purpose, and general description. Optionally, they can add feeding programs, vaccination schedules, production benchmarks, and housing guidelines. Custom breeds appear in the catalogue alongside pre-seeded breeds, clearly marked as farmer-created, and are visible only to the farmer who created them.

**Why this priority**: Custom breeds extend the catalogue's usefulness to farmers working with local varieties, but the core value is delivered by the pre-seeded breeds. This is an enhancement that increases adoption.

**Independent Test**: Can be tested by creating a custom breed with all optional sections, verifying it appears in the catalogue with a "custom" badge, and confirming it can be selected when creating a flock.

**Acceptance Scenarios**:

1. **Given** a farmer is on the catalogue page, **When** they select "Add Custom Breed," **Then** a form opens with fields for name, category, purpose, description, and an optional image upload.
2. **Given** a farmer is filling out a custom breed form, **When** they submit with only the required fields (name, category, purpose), **Then** the breed is created successfully and appears in the catalogue.
3. **Given** a farmer has created a custom breed, **When** they view the catalogue, **Then** the custom breed appears with a visual indicator distinguishing it from pre-seeded breeds.
4. **Given** a farmer has created custom breeds, **When** another farmer views the catalogue, **Then** they do not see the first farmer's custom breeds.
5. **Given** a farmer is viewing their custom breed, **When** they choose to edit it, **Then** they can update all fields including adding or modifying feeding programs, vaccination schedules, benchmarks, and housing guidelines.
6. **Given** a farmer wants to remove a custom breed, **When** they delete it and it is not currently assigned to any flock, **Then** the breed is removed from the catalogue.
7. **Given** a farmer wants to remove a custom breed assigned to an active flock, **When** they attempt to delete it, **Then** the system warns them and requires confirmation, explaining that flocks using this breed will retain their existing programs but lose the breed reference.

---

### User Story 5 - Compare Breeds Side by Side (Priority: P3)

A farmer selects two or three breeds to compare their profiles side by side. The comparison view shows key metrics aligned in columns, making it easy to evaluate differences in production benchmarks, feeding costs, housing requirements, and vaccination complexity.

**Why this priority**: Comparison is a valuable decision-support tool, but farmers can achieve similar results by viewing profiles individually. This is a convenience feature that enhances the user experience.

**Independent Test**: Can be tested by selecting two breeds from the catalogue, opening the comparison view, and verifying all key metrics are displayed in aligned columns.

**Acceptance Scenarios**:

1. **Given** a farmer is browsing the catalogue, **When** they select two or three breeds for comparison, **Then** a side-by-side comparison view opens showing key metrics in aligned columns.
2. **Given** the comparison view is open, **When** the farmer reviews it, **Then** they see production benchmarks, feeding program summaries, housing requirements, and vaccination counts for each breed.
3. **Given** the comparison view is open, **When** a metric differs significantly between breeds, **Then** the difference is visually highlighted to draw attention.
4. **Given** the farmer tries to compare more than three breeds, **When** they select a fourth breed, **Then** the system informs them that a maximum of three breeds can be compared at once.

---

### Edge Cases

- What happens when a farmer searches with special characters or very long search terms? The system sanitizes input and limits search terms to 100 characters.
- How does the system handle a pre-seeded breed that is updated in a future system release? Existing flocks retain their saved programs; the catalogue shows the updated breed profile; farmers are not retroactively affected.
- What happens if a custom breed has no template data and is selected for a new flock? The flock is created with empty program sections that the farmer must fill in manually.
- How does the catalogue handle breeds with multilingual names? Breed names are stored in Arabic, English, and French; the catalogue displays names in the user's active language.
- What happens when a farmer is offline and tries to browse the catalogue? Previously loaded catalogue data is available from the local cache; creating custom breeds is queued for sync when connectivity returns.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a searchable, filterable catalogue of poultry breed profiles accessible to all authenticated farmers.
- **FR-002**: System MUST ship with pre-seeded breed profiles for the most common commercial and heritage breeds in the Moroccan market (minimum 15 breeds).
- **FR-003**: Each breed profile MUST include: name (multilingual), category (commercial/heritage/custom), purpose (layers/broilers/dual-purpose), origin, general description, and optional image.
- **FR-004**: Each breed profile MUST include structured template data for: feeding programs (by growth stage), vaccination schedules, production benchmarks, and housing guidelines.
- **FR-005**: System MUST support full-text search across breed names and descriptions in all supported languages.
- **FR-006**: System MUST support filtering breeds by category, purpose, and custom/pre-seeded status.
- **FR-007**: System MUST allow authenticated farmers to create custom breed entries with at minimum a name, category, and purpose.
- **FR-008**: Custom breed entries MUST be visible only to the farmer who created them.
- **FR-009**: System MUST allow farmers to edit and delete their own custom breed entries.
- **FR-010**: System MUST prevent deletion of custom breeds that are assigned to active flocks without explicit farmer confirmation.
- **FR-011**: When a farmer creates a new flock and selects a breed, the system MUST auto-generate recommended feeding programs, vaccination schedules, production benchmarks, and housing guidelines from the breed's template data.
- **FR-012**: Auto-generated programs MUST be editable by the farmer before and after saving.
- **FR-013**: System MUST allow farmers to compare up to three breeds side by side, displaying key metrics in aligned columns.
- **FR-014**: Breed profiles and catalogue listings MUST display in the user's active language (Arabic, French, or English).
- **FR-015**: System MUST support offline access to previously loaded catalogue data.
- **FR-016**: When a farmer changes the breed on an existing flock, the system MUST offer the option to regenerate programs from the new breed's template or keep current programs.

### Key Entities

- **Breed**: Represents a poultry breed profile. Key attributes: name (multilingual), category, purpose, origin, description, image, whether it is pre-seeded or custom, and the owning farmer (for custom breeds). A breed contains one-to-many relationships with feeding program templates, vaccination schedule templates, production benchmarks, and housing guidelines.
- **Feeding Program Template**: A recommended feeding plan for a specific growth stage. Key attributes: growth stage (starter/grower/finisher), feed type, daily quantity per bird, feeding frequency. Belongs to one breed.
- **Vaccination Schedule Template**: A recommended vaccination entry. Key attributes: vaccine name, recommended age (in days), dosage, administration route, notes. Belongs to one breed.
- **Production Benchmark**: An expected performance metric. Key attributes: metric type (egg production rate, daily weight gain, feed conversion ratio, mortality rate), expected value, age range. Belongs to one breed.
- **Housing Guideline**: A recommended housing parameter. Key attributes: parameter name (space per bird, temperature range, humidity range, ventilation rate, lighting schedule), recommended value, unit of measure, notes. Belongs to one breed.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Farmers can find a specific breed in the catalogue within 10 seconds using search or filters.
- **SC-002**: 90% of farmers who create a new flock using a catalogued breed accept the auto-generated programs without modification, indicating the defaults are practical and trustworthy.
- **SC-003**: Flock creation time is reduced by at least 60% compared to manual entry when a breed is selected from the catalogue.
- **SC-004**: The catalogue launches with a minimum of 15 pre-seeded breed profiles covering the most common commercial and heritage breeds in the Moroccan market.
- **SC-005**: Farmers can create a custom breed entry in under 3 minutes with all required fields.
- **SC-006**: The breed comparison view enables farmers to make a breed selection decision within 2 minutes of opening the comparison.
- **SC-007**: Catalogue browsing and search remain responsive (results appear within 1 second) with up to 500 breeds in the system.
- **SC-008**: Offline catalogue access shows previously loaded breeds with no errors or blank screens when the device loses connectivity.

## Assumptions

- The Moroccan poultry market primarily uses well-documented commercial breeds (e.g., Sasso, Isa Brown, Cobb 500, Ross 308, Lohmann Brown) and heritage breeds (e.g., Beldi, Fayoumi), for which standard feeding, vaccination, and production data is publicly available.
- Breed template data (feeding programs, vaccination schedules, etc.) reflects general best practices and will include disclaimers that farmers should consult local veterinarians for region-specific guidance.
- Pre-seeded breed data will be maintained and updated by the system administrators; farmers cannot modify pre-seeded breeds.
- The existing flock creation workflow will be extended to integrate breed selection from the catalogue, replacing the current free-text breed field.
- Image uploads for custom breeds follow the same size and format constraints as other image uploads in the system.
- Pagination defaults to 12 breeds per page in the catalogue listing view.
