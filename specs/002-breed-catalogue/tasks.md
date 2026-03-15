# Tasks: Breed Catalogue

**Input**: Design documents from `specs/002-breed-catalogue/` (spec.md, plan.md, data-model.md, contracts/, research.md)
**Prerequisites**: spec.md (required), plan.md (required)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Backend**: `apps/backend/src/main/java/ma/farmsense/`
- **Frontend**: `apps/frontend/src/`
- **Migrations**: `apps/backend/src/main/resources/db/migration/`
- **E2E Tests**: `apps/frontend/e2e/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Database schema, enums, and shared entities that all user stories depend on

- [X] T001 Create Flyway migration `V16__breed_catalogue.sql` in `apps/backend/src/main/resources/db/migration/` — create `breeds` table with all columns per data-model.md (id, user_id FK, name, name_ar, name_en, category, purpose, origin, description/description_ar/description_en, image_url, climate_suitability, avg_weight_male_kg, avg_weight_female_kg, is_system, created_at, updated_at), create `feeding_program_templates` table (breed_id FK CASCADE, growth_stage, age_start_days, age_end_days, feed_type, daily_quantity_grams, feeding_frequency, notes, sort_order), create `vaccination_templates` table (breed_id FK CASCADE, vaccine_name, recommended_age_days, dosage, administration_route, is_mandatory, notes, sort_order), create `production_benchmarks` table (breed_id FK CASCADE, metric_type, expected_value, unit, age_start_days, age_end_days, notes, sort_order), create `housing_guidelines` table (breed_id FK CASCADE, parameter_name, recommended_value, unit, growth_stage, notes, sort_order), add `breed_id UUID REFERENCES breeds(id) ON DELETE SET NULL` column to `flocks` table, create all indexes per data-model.md including trigram indexes (enable `pg_trgm` extension)
- [X] T002 Create Flyway seed migration `V17__seed_breeds.sql` in `apps/backend/src/main/resources/db/migration/` — insert 15+ pre-seeded breeds (user_id=NULL, is_system=TRUE) with complete template data. Include: Sasso, Isa Brown, Cobb 500, Ross 308, Lohmann Brown, Hubbard, Arbor Acres, Beldi (heritage), Fayoumi (heritage), Plymouth Rock (heritage), Rhode Island Red (heritage), Leghorn, Cornish Cross, Sussex (heritage), Kabyle (heritage). Each breed must have at least 3 feeding program entries (starter/grower/finisher), 5+ vaccination entries, 3+ production benchmarks, and 4+ housing guidelines with realistic Moroccan-market data
- [X] T003 Create Flyway migration `V18__migrate_flock_breeds.sql` in `apps/backend/src/main/resources/db/migration/` — match existing `flocks.breed` text values to breed records by name (case-insensitive), set `breed_id`, then drop the `breed` column from `flocks` table
- [X] T004 [P] Create `BreedCategory` enum in `apps/backend/src/main/java/ma/farmsense/entity/BreedCategory.java` with values COMMERCIAL, HERITAGE, CUSTOM
- [X] T005 [P] Create `BreedPurpose` enum in `apps/backend/src/main/java/ma/farmsense/entity/BreedPurpose.java` with values LAYERS, BROILERS, DUAL_PURPOSE (separate from FlockPurpose per research R3)
- [X] T006 [P] Create `GrowthStage` enum in `apps/backend/src/main/java/ma/farmsense/entity/GrowthStage.java` with values STARTER, GROWER, FINISHER
- [X] T007 [P] Create `AdministrationRoute` enum in `apps/backend/src/main/java/ma/farmsense/entity/AdministrationRoute.java` with values ORAL, INJECTION, EYE_DROP, SPRAY, DRINKING_WATER

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core entities, repository, service, and controller that MUST be complete before ANY user story frontend work can begin

**⚠️ CRITICAL**: No user story frontend work can begin until this phase is complete

- [X] T008 Create `Breed` entity in `apps/backend/src/main/java/ma/farmsense/entity/Breed.java` — JPA entity mapped to `breeds` table with all fields per data-model.md, @ManyToOne(LAZY) to User (nullable), @OneToMany(CASCADE) to FeedingProgramTemplate/VaccinationTemplate/ProductionBenchmark/HousingGuideline, use Lombok @Entity @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor, @PreUpdate for updatedAt
- [X] T009 [P] Create `FeedingProgramTemplate` entity in `apps/backend/src/main/java/ma/farmsense/entity/FeedingProgramTemplate.java` — JPA entity with @ManyToOne(LAZY) breed_id, GrowthStage enum, all fields per data-model.md
- [X] T010 [P] Create `VaccinationTemplate` entity in `apps/backend/src/main/java/ma/farmsense/entity/VaccinationTemplate.java` — JPA entity with @ManyToOne(LAZY) breed_id, AdministrationRoute enum, all fields per data-model.md
- [X] T011 [P] Create `ProductionBenchmark` entity in `apps/backend/src/main/java/ma/farmsense/entity/ProductionBenchmark.java` — JPA entity with @ManyToOne(LAZY) breed_id, all fields per data-model.md
- [X] T012 [P] Create `HousingGuideline` entity in `apps/backend/src/main/java/ma/farmsense/entity/HousingGuideline.java` — JPA entity with @ManyToOne(LAZY) breed_id, all fields per data-model.md
- [X] T013 Create `BreedRepository` in `apps/backend/src/main/java/ma/farmsense/repository/BreedRepository.java` — Spring Data JPA repository with: findByIsSystemTrueOrUserOrderByNameAsc (for catalogue listing with user custom breeds), Page<Breed> search method with ILIKE on name/name_ar/name_en/description/description_ar/description_en, filter methods for category and purpose, countByUserAndNameIgnoreCase (for uniqueness check), findByIsSystemTrueAndNameIgnoreCase (for migration matching)
- [X] T014 [P] Create `BreedSummaryResponse` record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/BreedSummaryResponse.java` — Java record with fields matching API contract list response (id, name, nameAr, nameEn, category, purpose, origin, imageUrl, isSystem, climateSuitability, avgWeightMaleKg, avgWeightFemaleKg), static `from(Breed)` factory method
- [X] T015 [P] Create `BreedDetailResponse` record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/BreedDetailResponse.java` — Java record with all breed fields plus nested lists: feedingPrograms, vaccinationSchedule, productionBenchmarks, housingGuidelines (using child DTOs), static `from(Breed)` factory method
- [X] T016 [P] Create `FeedingProgramTemplateDto` record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/FeedingProgramTemplateDto.java` — Java record with all feeding template fields, static `from(FeedingProgramTemplate)` factory, Jakarta validation on components (@NotNull growthStage, @NotNull @Min(0) ageStartDays, @NotNull ageEndDays, @NotBlank feedType, @NotNull @DecimalMin("0.01") dailyQuantityGrams, @NotNull @Min(1) feedingFrequency)
- [X] T017 [P] Create `VaccinationTemplateDto` record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/VaccinationTemplateDto.java` — Java record with all vaccination fields, static `from(VaccinationTemplate)` factory, Jakarta validation (@NotBlank vaccineName, @NotNull @Min(1) recommendedAgeDays, @NotNull administrationRoute)
- [X] T018 [P] Create `ProductionBenchmarkDto` record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/ProductionBenchmarkDto.java` — Java record with all benchmark fields, static `from(ProductionBenchmark)` factory, Jakarta validation (@NotBlank metricType, @NotNull @DecimalMin("0.0001") expectedValue, @NotBlank unit)
- [X] T019 [P] Create `HousingGuidelineDto` record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/HousingGuidelineDto.java` — Java record with all housing fields, static `from(HousingGuideline)` factory, Jakarta validation (@NotBlank parameterName, @NotBlank recommendedValue)
- [X] T020 [P] Create `CreateBreedRequest` record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/CreateBreedRequest.java` — Java record with @NotBlank @Size(max=255) name, @Size(max=255) nameAr/nameEn, @NotNull category (forced CUSTOM), @NotNull purpose, optional origin/description/descriptionAr/descriptionEn/imageUrl/climateSuitability/avgWeightMaleKg/avgWeightFemaleKg, optional List<FeedingProgramTemplateDto> feedingPrograms, List<VaccinationTemplateDto> vaccinationSchedule, List<ProductionBenchmarkDto> productionBenchmarks, List<HousingGuidelineDto> housingGuidelines
- [X] T021 [P] Create `UpdateBreedRequest` record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/UpdateBreedRequest.java` — Java record with all fields optional (same structure as Create but no @NotNull/@NotBlank), for partial update pattern
- [X] T022 Create `BreedService` in `apps/backend/src/main/java/ma/farmsense/service/BreedService.java` — implement: findAll(User, search, category, purpose, page, size) returning Page<BreedSummaryResponse> — query system breeds + user's custom breeds, apply search/filters; findById(User, UUID) returning BreedDetailResponse — load with child collections, check visibility (system or owned); create(User, CreateBreedRequest) — force is_system=false and user_id, check name uniqueness per user, persist breed + child entities; update(User, UUID, UpdateBreedRequest) — verify ownership and not system breed, partial update pattern; delete(User, UUID, boolean confirm) — verify ownership and not system, check active flock count, require confirm if in use, hard delete with CASCADE; getTemplates(UUID) returning template data for flock creation. All write methods @Transactional. Follow getOwned pattern from FlockService
- [X] T023 Create `BreedController` in `apps/backend/src/main/java/ma/farmsense/controller/BreedController.java` — REST controller at `/api/v1/breeds` with: GET / (list with search/category/purpose/page/size query params), GET /{id} (detail), POST / (create with @Valid), PUT /{id} (update with @Valid), DELETE /{id} (delete with ?confirm param), GET /{id}/templates (template data). All methods take @AuthenticationPrincipal User. Follow FlockController patterns exactly
- [X] T024 Update `Flock` entity in `apps/backend/src/main/java/ma/farmsense/entity/Flock.java` — add @ManyToOne(LAZY) breed field with @JoinColumn(name="breed_id"), remove the old `private String breed` field
- [X] T025 Update `CreateFlockRequest` in `apps/backend/src/main/java/ma/farmsense/dto/poultry/CreateFlockRequest.java` — replace `String breed` with `UUID breedId` (optional)
- [X] T026 Update `UpdateFlockRequest` in `apps/backend/src/main/java/ma/farmsense/dto/poultry/UpdateFlockRequest.java` — replace `String breed` with `UUID breedId` (optional)
- [X] T027 Update `FlockResponse` in `apps/backend/src/main/java/ma/farmsense/dto/poultry/FlockResponse.java` — replace `String breed` with `UUID breedId`, `String breedName`, `String breedImageUrl`, update `from()` factory to extract from breed relation
- [X] T028 Update `FlockService` in `apps/backend/src/main/java/ma/farmsense/service/FlockService.java` — inject BreedRepository, in create(): resolve breedId to Breed entity and set on flock, in update(): handle breedId changes (set new breed reference), update all field mappings from breed String to breed entity
- [X] T029 Add i18n translation keys for breed catalogue in `apps/frontend/src/i18n/fr.ts`, `apps/frontend/src/i18n/ar.ts`, `apps/frontend/src/i18n/en.ts` — add keys for: breed_catalogue, breed_search_placeholder, breed_filter_category, breed_filter_purpose, breed_categories (commercial/heritage/custom), breed_purposes (layers/broilers/dual_purpose), breed_detail sections (feeding_program, vaccination_schedule, production_benchmarks, housing_guidelines), breed_form labels, breed_compare, breed_custom_badge, breed_empty_state, breed_delete_confirm, breed_in_use_warning, growth_stages (starter/grower/finisher), administration_routes
- [X] T030 Add Breed TypeScript types in `apps/frontend/src/types/index.ts` — add interfaces: BreedSummary (matching API list response), BreedDetail (matching API detail response), FeedingProgramTemplate, VaccinationTemplate, ProductionBenchmark, HousingGuideline, CreateBreedRequest, UpdateBreedRequest, BreedCategory enum, BreedPurpose enum, GrowthStage enum, AdministrationRoute enum
- [X] T031 Create `breeds.store.ts` in `apps/frontend/src/stores/breeds.store.ts` — Pinia store (composition API) with: state for breeds list (paginated), currentBreed (detail), comparedBreeds (array max 3), loading/error states, search/filter state; actions for fetchBreeds(search, category, purpose, page, size), fetchBreedById(id), fetchBreedTemplates(id), createBreed(data), updateBreed(id, data), deleteBreed(id, confirm), addToCompare(id)/removeFromCompare(id)/clearCompare(); use api.ts service for all HTTP calls with proper error handling

**Checkpoint**: Backend API fully functional, frontend types/store/i18n ready — user story frontend work can begin

---

## Phase 3: User Story 1 — Browse and Search Breed Catalogue (Priority: P1) 🎯 MVP

**Goal**: Farmers can open the catalogue, see breed cards, search by name, and filter by category/purpose

**Independent Test**: Navigate to `/poultry/breeds`, verify breed cards display with pre-seeded data, search returns matching results, filters narrow the list, empty state shows when no matches

### Implementation for User Story 1

- [X] T032 [US1] Add breed catalogue routes in `apps/frontend/src/router/index.ts` — add `/poultry/breeds` route pointing to BreedsView, add `/poultry/breeds/:id` route pointing to BreedDetailView (both under authenticated layout)
- [X] T033 [US1] Create `BreedCard.vue` component in `apps/frontend/src/components/poultry/BreedCard.vue` — card displaying breed thumbnail image (or placeholder), name (in user's active language), category badge (commercial/heritage/custom with distinct colors), purpose label, origin, avg weight range. Emit click event for navigation. Include compare checkbox (for US5). Use Tailwind 4 classes, support RTL layout
- [X] T034 [US1] Create `BreedsView.vue` in `apps/frontend/src/views/poultry/BreedsView.vue` — catalogue listing page with: search input field (debounced 300ms, max 100 chars), category filter dropdown (All/Commercial/Heritage/Custom), purpose filter dropdown (All/Layers/Broilers/Dual-Purpose), responsive grid of BreedCard components (3 cols desktop, 2 tablet, 1 mobile), pagination controls (12 per page), loading skeleton state, empty state with message suggesting to broaden search or create custom breed, "Add Custom Breed" button. Wire to breeds store actions. Support RTL layout

**Checkpoint**: User Story 1 complete — farmers can browse, search, and filter the breed catalogue

---

## Phase 4: User Story 2 — View Breed Profile Details (Priority: P1)

**Goal**: Farmers can view a full breed profile with all four structured template sections (feeding, vaccination, benchmarks, housing)

**Independent Test**: Click any breed card, verify profile page shows general info + all four sections with complete data, verify multilingual names display in user's language

### Implementation for User Story 2

- [ ] T035 [US2] Create `BreedTemplateSection.vue` in `apps/frontend/src/components/poultry/BreedTemplateSection.vue` — reusable collapsible section component with title, icon, and content slot. Used to wrap each of the four template sections on the detail page. Tailwind 4 styling, RTL support
- [ ] T036 [US2] Create `BreedDetailView.vue` in `apps/frontend/src/views/poultry/BreedDetailView.vue` — full breed profile page with: breed header (name in active language, category badge, purpose, origin, image, climate suitability, weight ranges), four BreedTemplateSection instances: (1) Feeding Program — table showing growth stages with age range, feed type, daily quantity, frequency; (2) Vaccination Schedule — chronological list with vaccine name, age in days, dosage, route, mandatory badge; (3) Production Benchmarks — metrics table with type, expected value, unit, age range; (4) Housing Guidelines — parameter list with name, recommended value, unit, stage. Back button to catalogue. "Select for Flock" action button. Wire to breeds store fetchBreedById. Support RTL layout

**Checkpoint**: User Story 2 complete — farmers can view complete breed profiles

---

## Phase 5: User Story 3 — Auto-Generate Flock Programs from Breed Selection (Priority: P1)

**Goal**: When creating/editing a flock and selecting a breed, the system auto-generates recommended programs from the breed's template data

**Independent Test**: Create a new flock, select a pre-seeded breed, verify feeding/vaccination/benchmark/housing programs are auto-populated, verify programs are editable, verify saving preserves the data

### Implementation for User Story 3

- [ ] T037 [US3] Update flock creation form in `apps/frontend/src/components/poultry/FlockForm.vue` (or equivalent existing flock form component) — replace free-text breed input with a breed selector that opens a searchable breed picker (mini catalogue view or dropdown with search). When a breed is selected: call breeds store fetchBreedTemplates(breedId), display auto-generated programs in editable form sections (feeding, vaccination, benchmarks, housing). Show loading state while templates load. If breed has incomplete data, show empty sections marked for manual entry. Programs should be editable inline before saving
- [ ] T038 [US3] Update flock edit form — when breedId changes on an existing flock, show a confirmation dialog: "Breed changed. Regenerate programs from new breed template or keep current programs?" with two buttons. If regenerate: fetch new templates and replace form data. If keep: retain current values
- [ ] T039 [US3] Update `apps/frontend/src/stores/flocks.store.ts` (or equivalent flock store) — update createFlock action to include breedId in request payload, update updateFlock action to include breedId, add handling for breedChanged response flag

**Checkpoint**: User Story 3 complete — breed selection in flock creation auto-generates programs

---

## Phase 6: User Story 4 — Create Custom Breed Entry (Priority: P2)

**Goal**: Farmers can create, edit, and delete custom breed entries for local crossbreeds or uncommon varieties

**Independent Test**: Click "Add Custom Breed", fill required fields, save, verify it appears in catalogue with custom badge. Edit it, verify changes persist. Delete it, verify removal. Create a flock with the custom breed, try to delete the breed, verify confirmation is required

### Implementation for User Story 4

- [ ] T040 [US4] Create `BreedForm.vue` in `apps/frontend/src/components/poultry/BreedForm.vue` — form component for creating/editing custom breeds with: required fields (name, category locked to CUSTOM, purpose dropdown), optional fields (nameAr, nameEn, origin, description/descriptionAr/descriptionEn, imageUrl, climateSuitability, avgWeightMaleKg, avgWeightFemaleKg), collapsible sections for adding template data (feeding programs, vaccination schedule, benchmarks, housing guidelines) — each section with add/remove/edit rows. Client-side validation matching backend rules. Submit handler that calls breeds store create/update. Support RTL layout
- [ ] T041 [US4] Add custom breed create/edit views — either modal or route-based. Wire "Add Custom Breed" button in BreedsView to open BreedForm in create mode. Wire "Edit" button on BreedDetailView (visible only for user-owned custom breeds) to open BreedForm in edit mode pre-populated with current data
- [ ] T042 [US4] Implement breed deletion flow — "Delete" button on BreedDetailView (visible only for user-owned custom breeds). On click: check if breed is assigned to active flocks (from delete endpoint 409 response). If in use: show warning dialog with flock count and require confirmation. If confirmed or not in use: call deleteBreed(id, confirm=true). On success: redirect to catalogue with success toast. Handle errors with user-friendly messages

**Checkpoint**: User Story 4 complete — farmers can manage custom breeds

---

## Phase 7: User Story 5 — Compare Breeds Side by Side (Priority: P3)

**Goal**: Farmers can select 2-3 breeds and compare key metrics in aligned columns

**Independent Test**: Select two breeds using compare checkboxes on catalogue, open comparison view, verify metrics display in aligned columns, verify max 3 breed limit is enforced

### Implementation for User Story 5

- [ ] T043 [US5] Create `BreedCompareView.vue` in `apps/frontend/src/components/poultry/BreedCompareView.vue` — side-by-side comparison component showing: breed names and images as column headers, general info row (category, purpose, origin, weight ranges, climate), feeding program comparison (aligned by growth stage), vaccination count comparison, production benchmark comparison (aligned by metric type with visual highlighting for significant differences), housing guideline comparison (aligned by parameter). Responsive: on mobile show stacked cards instead of columns. Support RTL layout
- [ ] T044 [US5] Add compare UI to BreedsView — floating "Compare (N)" bar that appears when 1+ breeds are checked for comparison. Bar shows count (max 3) and "Compare Now" button. When a 4th breed is selected, show toast message "Maximum 3 breeds can be compared". "Compare Now" navigates to or opens BreedCompareView with selected breed IDs. Clear button to deselect all. Wire to breeds store comparedBreeds state

**Checkpoint**: User Story 5 complete — farmers can compare breeds side by side

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Offline support, Workbox caching, and final quality improvements

- [ ] T045 [P] Configure Workbox caching for breed catalogue endpoints — add `/api/v1/breeds` (list) and `/api/v1/breeds/*` (detail) to Workbox runtime caching in service worker config (StaleWhileRevalidate or CacheFirst strategy). Ensure catalogue is available offline from cache
- [ ] T046 [P] Add PouchDB offline queue support for custom breed creation — queue POST `/api/v1/breeds` requests when offline, replay on reconnect following existing offline-queue.ts pattern in `apps/frontend/src/services/offline-queue.ts`
- [ ] T047 [P] Add navigation entry for breed catalogue — add "Breed Catalogue" menu item to the poultry section of the main navigation/sidebar in the appropriate layout component, with icon and i18n label
- [ ] T048 [P] Create Playwright E2E test in `apps/frontend/e2e/breeds.spec.ts` — test scenarios: (1) navigate to catalogue and verify breed cards render, (2) search by name and verify filtering, (3) filter by category/purpose, (4) click breed card and verify detail page shows all sections, (5) create custom breed and verify it appears, (6) edit custom breed, (7) delete custom breed without flock reference, (8) select breed in flock creation and verify programs auto-populate, (9) compare two breeds side by side
- [ ] T049 Verify Sentry error tracking captures breed-related errors — ensure controller exceptions are properly mapped and Sentry captures 400/403/404/409 responses with context

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately. T001→T002→T003 must be sequential (migration order). T004-T007 can run in parallel with each other
- **Foundational (Phase 2)**: Depends on Phase 1 completion. T008 depends on T004-T007 (enums). T009-T012 depend on T008 (Breed entity). T013 depends on T008. T014-T021 can run in parallel (independent DTOs). T022 depends on T008, T013, T014-T021. T023 depends on T022. T024-T028 depend on T008 (flock updates). T029-T031 can run in parallel (frontend setup)
- **User Story 1 (Phase 3)**: Depends on Phase 2. T032→T033→T034
- **User Story 2 (Phase 4)**: Depends on Phase 2. Can run in parallel with US1. T035→T036
- **User Story 3 (Phase 5)**: Depends on Phase 2 + existing flock form. T037→T038→T039
- **User Story 4 (Phase 6)**: Depends on Phase 2. Can run in parallel with US1/US2. T040→T041→T042
- **User Story 5 (Phase 7)**: Depends on US1 (catalogue with compare checkboxes in BreedCard). T043→T044
- **Polish (Phase 8)**: Depends on all user stories complete. All tasks can run in parallel

### User Story Dependencies

- **US1 (P1)**: Can start after Phase 2 — no dependencies on other stories
- **US2 (P1)**: Can start after Phase 2 — no dependencies on other stories
- **US3 (P1)**: Can start after Phase 2 — uses existing flock form components
- **US4 (P2)**: Can start after Phase 2 — no dependencies on other stories
- **US5 (P3)**: Depends on US1 (BreedCard.vue compare checkbox) and US2 (detail data loading)

### Within Each User Story

- Models/types before services
- Services before views/components
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- Phase 1: T004, T005, T006, T007 can all run in parallel
- Phase 2: T009, T010, T011, T012 in parallel; T014-T021 in parallel; T029, T030, T031 in parallel
- Phase 3-6: US1, US2, US3, US4 can all start in parallel after Phase 2
- Phase 8: All polish tasks can run in parallel

---

## Parallel Example: Phase 2 (Foundational)

```bash
# Launch all child entity files together:
Task T009: "Create FeedingProgramTemplate entity"
Task T010: "Create VaccinationTemplate entity"
Task T011: "Create ProductionBenchmark entity"
Task T012: "Create HousingGuideline entity"

# Launch all DTO records together:
Task T014: "Create BreedSummaryResponse record"
Task T015: "Create BreedDetailResponse record"
Task T016: "Create FeedingProgramTemplateDto record"
Task T017: "Create VaccinationTemplateDto record"
Task T018: "Create ProductionBenchmarkDto record"
Task T019: "Create HousingGuidelineDto record"
Task T020: "Create CreateBreedRequest record"
Task T021: "Create UpdateBreedRequest record"

# Launch frontend setup together:
Task T029: "Add i18n keys for breed catalogue"
Task T030: "Add Breed TypeScript types"
Task T031: "Create breeds Pinia store"
```

---

## Implementation Strategy

### MVP First (User Stories 1 + 2 + 3)

1. Complete Phase 1: Setup (migrations + enums)
2. Complete Phase 2: Foundational (entities, DTOs, service, controller, frontend store)
3. Complete Phase 3: US1 — Browse & Search (catalogue listing)
4. Complete Phase 4: US2 — Breed Detail (profile view)
5. Complete Phase 5: US3 — Auto-Generate Programs (flock integration)
6. **STOP and VALIDATE**: Test all three P1 stories independently
7. Deploy/demo if ready — core catalogue functionality is complete

### Incremental Delivery

1. Setup + Foundational → API and store ready
2. Add US1 → Catalogue browsing works → Demo
3. Add US2 → Breed profiles viewable → Demo
4. Add US3 → Flock creation integrates with catalogue → Demo (MVP!)
5. Add US4 → Custom breeds supported → Demo
6. Add US5 → Comparison view available → Demo
7. Polish → Offline support, E2E tests, navigation → Release

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: US1 (catalogue listing) + US5 (comparison — depends on US1)
   - Developer B: US2 (breed detail) + US4 (custom breeds)
   - Developer C: US3 (flock integration) + Polish
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- All DTOs MUST be Java records with `from()` factory methods (Constitution VIII)
- All new UI strings MUST have i18n keys in FR/AR/EN (Constitution II)
- All new endpoints MUST require JWT auth (Constitution V)
- Flock entity changes (T024-T028) update existing files — coordinate with any in-flight flock work
