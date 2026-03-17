# Tasks: Flock Registry

**Feature**: 003-flock-registry
**Input**: `/specs/003-flock-registry/` — plan.md, spec.md, data-model.md, contracts/api-contracts.md
**Tech Stack**: Java 21 / Spring Boot 3.4.x (backend) · TypeScript strict / Vue 3 / Pinia / Tailwind CSS 4 (frontend)
**Storage**: PostgreSQL 15 via Flyway V19

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies on incomplete tasks)
- **[Story]**: User story label ([US1]–[US5])
- All tasks include exact file paths

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Confirm monorepo structure is ready and feature branch is active.

- [X] T001 Verify feature branch `003-flock-registry` is checked out and `specs/003-flock-registry/` directory exists
- [X] T002 Confirm latest Flyway migration is V18 (so V19 is the correct next version) by inspecting `apps/backend/src/main/resources/db/migration/`
- [X] T003 [P] Confirm frontend dev server starts cleanly with `npm run dev` in `apps/frontend/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Database schema, new enums, entities, and repositories that ALL user stories depend on.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [X] T004 Write Flyway migration `apps/backend/src/main/resources/db/migration/V19__flock_registry_enhancements.sql`:
  - Create `housing_locations` table (id, user_id FK, name, name_ar, name_en, location_type, notes, created_at, updated_at)
  - Add `housing_location_id UUID REFERENCES housing_locations(id) ON DELETE SET NULL` column to `flocks`
  - Add `batch_code VARCHAR(100) NOT NULL DEFAULT ''` column to `flocks`
  - Add partial unique index `uq_flock_batch_code ON flocks(user_id, batch_code) WHERE batch_code <> ''`
  - Add `type VARCHAR(20) NOT NULL DEFAULT 'NATURAL_DEATH'` column to `mortality_records`
- [X] T005 [P] Extend `FlockPurpose` enum in `apps/backend/src/main/java/ma/farmsense/entity/FlockPurpose.java`: add `DUAL_PURPOSE`, `BREEDERS`
- [X] T006 [P] Extend `FlockStatus` enum in `apps/backend/src/main/java/ma/farmsense/entity/FlockStatus.java`: add `PHASED_OUT` (keep `FINISHED` for legacy)
- [X] T007 [P] Create `HousingLocationType` enum in `apps/backend/src/main/java/ma/farmsense/entity/HousingLocationType.java`: `COOP`, `PEN`, `FREE_RANGE`
- [X] T008 [P] Create `MortalityType` enum in `apps/backend/src/main/java/ma/farmsense/entity/MortalityType.java`: `NATURAL_DEATH`, `CULL`
- [X] T009 [P] Create `MortalityCause` enum in `apps/backend/src/main/java/ma/farmsense/entity/MortalityCause.java`: `DISEASE`, `INJURY`, `PREDATOR`, `LOW_WEIGHT`, `HEAT_STRESS`, `UNKNOWN`, `OTHER`
- [X] T010 Create `HousingLocation` JPA entity in `apps/backend/src/main/java/ma/farmsense/entity/HousingLocation.java` (after T007): fields: id (UUID), user (ManyToOne lazy), name, nameAr, nameEn, locationType (HousingLocationType), notes, createdAt, updatedAt with `@PreUpdate`
- [X] T011 Create `MortalityRecord` JPA entity in `apps/backend/src/main/java/ma/farmsense/entity/MortalityRecord.java` (after T008, T009): maps existing `mortality_records` table; fields: id, flock (ManyToOne lazy), type (MortalityType), count, mortalityDate, cause (MortalityCause nullable), notes, createdAt, updatedAt
- [X] T012 Modify `Flock` entity in `apps/backend/src/main/java/ma/farmsense/entity/Flock.java` (after T010): add `batchCode` String field (`@Column(name="batch_code")`); add `housingLocation` ManyToOne lazy FK field
- [X] T013 [P] Create `HousingLocationRepository` in `apps/backend/src/main/java/ma/farmsense/repository/HousingLocationRepository.java` (after T010): `findByUserOrderByCreatedAtDesc`, `findByUserAndLocationTypeOrderByCreatedAtDesc`, `findByIdAndUser`
- [X] T014 [P] Create `MortalityRecordRepository` in `apps/backend/src/main/java/ma/farmsense/repository/MortalityRecordRepository.java` (after T011): `findByFlockOrderByMortalityDateDesc`
- [X] T015 [P] Extend frontend types in `apps/frontend/src/types/index.ts`: expand `FlockPurpose` union (`DUAL_PURPOSE`, `BREEDERS`), expand `FlockStatus` union (`PHASED_OUT`), add `batchCode`/`housingLocationId`/`housingLocationName`/`ageWeeks` to `Flock`; add `HousingLocation`, `HousingLocationType`, `CreateHousingLocationRequest`, `UpdateHousingLocationRequest`, `MortalityType`, `MortalityCause`, `MortalityEvent`, `MortalityEventCreateResponse`, `CreateMortalityEventRequest` types
- [X] T016 [P] Extend Pinia store in `apps/frontend/src/stores/poultry.store.ts` (after T015): add `housingLocations` state and CRUD actions (`fetchHousingLocations`, `createHousingLocation`, `updateHousingLocation`, `deleteHousingLocation`); add `mortalityEvents` state and actions (`fetchMortalityEvents`, `createMortalityEvent`); update `deleteFlock` to set status `PHASED_OUT`

**Checkpoint**: V19 migration can be applied, all enums compile, all repositories exist, frontend types are in place → user story work can begin.

---

## Phase 3: User Story 1 — Register a New Flock (Priority: P1) 🎯 MVP

**Goal**: Operator can create a flock with name, unique batch code, breed, purpose (including new DUAL_PURPOSE/BREEDERS), housing location, headcount, and start date.

**Independent Test**: Create a housing location → create a flock with a unique batch code → verify flock appears in the list with all entered data and status ACTIVE. Attempt duplicate batch code → verify 409 error.

### Implementation for User Story 1

- [X] T017 [P] [US1] Create `CreateHousingLocationRequest` Java record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/CreateHousingLocationRequest.java`: fields `name` (@NotBlank @Size(max=255)), `nameAr`, `nameEn`, `locationType` (@NotNull HousingLocationType), `notes`
- [X] T018 [P] [US1] Create `UpdateHousingLocationRequest` Java record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/UpdateHousingLocationRequest.java`: all fields optional
- [X] T019 [P] [US1] Create `HousingLocationResponse` Java record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/HousingLocationResponse.java`: include `currentFlockCount` (long) and static `from(HousingLocation, long)` factory
- [X] T020 [US1] Create `HousingLocationService` in `apps/backend/src/main/java/ma/farmsense/service/HousingLocationService.java` (after T013, T017–T019): `findAll(User, HousingLocationType)`, `findById(User, UUID)`, `create(User, CreateHousingLocationRequest)`, `update(User, UUID, UpdateHousingLocationRequest)`, `delete(User, UUID)` — delete throws 409 if active flocks assigned
- [X] T021 [US1] Create `HousingLocationController` in `apps/backend/src/main/java/ma/farmsense/controller/HousingLocationController.java` (after T020): `GET /api/v1/housing-locations` (optional `?type=`), `POST /`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`
- [X] T022 [P] [US1] Modify `CreateFlockRequest` Java record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/CreateFlockRequest.java` (after T005): add `batchCode` (@NotBlank @Size(max=100)), `housingLocationId` (UUID, nullable)
- [X] T023 [P] [US1] Modify `FlockResponse` Java record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/FlockResponse.java` (after T005, T010): add `batchCode`, `housingLocationId`, `housingLocationName`, `ageWeeks` (Integer, computed in `from()` factory via `ChronoUnit.WEEKS.between(startDate, LocalDate.now())`)
- [X] T024 [US1] Modify `FlockService.create()` in `apps/backend/src/main/java/ma/farmsense/service/FlockService.java` (after T012–T013, T022): validate batch code uniqueness per user (`existsByUserAndBatchCode` → 409 if duplicate); resolve `housingLocationId` to `HousingLocation` entity
- [X] T025 [P] [US1] Create `HousingLocationForm.vue` component in `apps/frontend/src/components/poultry/HousingLocationForm.vue` (after T015): modal form with name input (required), locationType select (COOP/PEN/FREE_RANGE), notes textarea; emits `submit` with `CreateHousingLocationRequest` and `close`
- [X] T026 [P] [US1] Create `HousingLocationsView.vue` in `apps/frontend/src/views/poultry/HousingLocationsView.vue` (after T016, T025): grid of housing location cards showing name, type, currentFlockCount; create/edit/delete with `HousingLocationForm`; empty state
- [X] T027 [US1] Add route `{ path: 'poultry/housing', name: 'housing-locations', component: HousingLocationsView }` to `apps/frontend/src/router/index.ts`
- [X] T028 [US1] Modify `FlockForm.vue` in `apps/frontend/src/components/poultry/FlockForm.vue` (after T015, T016): add `batchCode` text field (required on create, readonly on edit); add housing location select populated from `poultryStore.housingLocations`; add `DUAL_PURPOSE` and `BREEDERS` purpose options; add `PHASED_OUT` status option (edit mode only)
- [X] T029 [US1] Add all US1-related i18n keys to `apps/frontend/src/i18n/index.ts` for FR, AR, and Darija: `flock_batch_code`, `flock_purpose_dual_purpose`, `flock_purpose_breeders`, `housing_locations_title`, `housing_location`, `housing_location_create`, `housing_locations_empty`, `housing_location_name`, `housing_location_type`, `housing_location_type_coop`, `housing_location_type_pen`, `housing_location_type_free_range`, `housing_location_flocks`
- [X] T030 [US1] Write `HousingLocationServiceTest` in `apps/backend/src/test/java/ma/farmsense/HousingLocationServiceTest.java` (after T020): 6 tests — create success, delete success, delete blocked by active flocks (409), findById not found (404), update success, findAll with type filter

**Checkpoint**: Can create a housing location at `/poultry/housing`, then create a flock with batch code and housing assignment. Duplicate batch code shows 409 error.

---

## Phase 4: User Story 2 — View Flock List and Details (Priority: P2)

**Goal**: Operator can view all flocks with age-in-weeks, filter by status/purpose, and drill into a flock detail page showing full profile including computed age.

**Independent Test**: With at least one flock created (start date N weeks ago), open the flock list → verify age chip shows N weeks → filter by ACTIVE → only active flocks shown → open detail page → batchCode, ageWeeks, and housing location are all visible.

### Implementation for User Story 2

- [X] T031 [P] [US2] Modify `FlockCard.vue` in `apps/frontend/src/components/poultry/FlockCard.vue` (after T015): display `batchCode` in font-mono under name; add `ageWeeks` chip (`Nw` orange badge, only when `ageWeeks != null`); show `housingLocationName` in info bar; add `PHASED_OUT` → amber badge to `statusClass` computed; add `purposeEmoji` computed (🥚 LAYERS, 🍗 BROILERS, 🐔 DUAL_PURPOSE, 🐣 BREEDERS)
- [X] T032 [P] [US2] Modify `FlocksView.vue` in `apps/frontend/src/views/poultry/FlocksView.vue` (after T015): add `PHASED_OUT` to status filter options; add `DUAL_PURPOSE` and `BREEDERS` to purpose filter options
- [X] T033 [P] [US2] Modify `FlockDetailView.vue` in `apps/frontend/src/views/poultry/FlockDetailView.vue` (after T015, T023): show `batchCode` (font-mono orange) and `ageWeeks` badge in header; add `PHASED_OUT` → amber to `statusClass`
- [X] T034 [US2] Add i18n keys for US2 to `apps/frontend/src/i18n/index.ts` (FR, AR, Darija): `flock_age_weeks`, `flock_status_phased_out`
- [ ] T035 [P] [US2] Add Playwright E2E tests for US2 in `apps/frontend/e2e/poultry.spec.ts`: batch code visible on flock card; age chip displays; status filter hides PHASED_OUT flocks from ACTIVE view; purpose filter for DUAL_PURPOSE and BREEDERS works

**Checkpoint**: Flock list shows age chip, batch code, housing. Filtering by status/purpose returns correct subsets. Detail page shows all new fields.

---

## Phase 5: User Story 3 — Mortality & Cull Logging (Priority: P3)

**Goal**: Operator can log bird losses (deaths or culls) against a flock, and the current headcount auto-decrements. Full event history is retained.

**Independent Test**: Create a flock with 500 birds → log 5 deaths with cause DISEASE → verify `currentBirdCount` is 495 on flock detail → attempt to log 600 deaths → verify 422 error. Open Mortality tab → verify event appears in history.

### Implementation for User Story 3

- [X] T036 [P] [US3] Create `CreateMortalityEventRequest` Java record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/CreateMortalityEventRequest.java` (after T008, T009): `type` (@NotNull MortalityType), `count` (@NotNull @Min(1) Integer), `mortalityDate` (@NotNull LocalDate), `cause` (MortalityCause nullable), `notes` (String nullable)
- [X] T037 [P] [US3] Create `MortalityEventResponse` Java record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/MortalityEventResponse.java` (after T008, T009): id, flockId, type, count, mortalityDate, cause, notes, createdAt; static `from(MortalityRecord)` factory
- [X] T038 [P] [US3] Create `MortalityEventCreateResponse` Java record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/MortalityEventCreateResponse.java`: extends `MortalityEventResponse` fields + `updatedFlockHeadcount` (int)
- [X] T039 [US3] Create `MortalityService` in `apps/backend/src/main/java/ma/farmsense/service/MortalityService.java` (after T014, T036–T038): `findByFlock(User, UUID flockId)` → `List<MortalityEventResponse>`; `@Transactional create(User, UUID flockId, CreateMortalityEventRequest)` → validates flock ACTIVE (409 if not), validates count ≤ currentBirdCount (422 if exceeded), persists MortalityRecord, decrements flock.currentBirdCount, calls `flockService.saveHeadcount(flock)`, returns `MortalityEventCreateResponse` with `updatedFlockHeadcount`
- [X] T040 [US3] Add `saveHeadcount(Flock)` @Transactional helper to `apps/backend/src/main/java/ma/farmsense/service/FlockService.java` (after T039 dependency)
- [X] T041 [US3] Create `MortalityController` in `apps/backend/src/main/java/ma/farmsense/controller/MortalityController.java` (after T039): `GET /api/v1/flocks/{flockId}/mortality-events`, `POST /api/v1/flocks/{flockId}/mortality-events`
- [X] T042 [P] [US3] Create `MortalityEventForm.vue` in `apps/frontend/src/components/poultry/MortalityEventForm.vue` (after T015, T016): modal with type select (NATURAL_DEATH/CULL), count number input (min 1, max currentBirdCount), date picker (default today), cause select (7 options + blank), notes textarea; emits `submit` with `CreateMortalityEventRequest` and `close`
- [X] T043 [P] [US3] Create `MortalityEventList.vue` in `apps/frontend/src/components/poultry/MortalityEventList.vue` (after T015): table of mortality events with columns: date, type badge (red NATURAL_DEATH / amber CULL), count, cause, notes; empty state; loading spinner
- [X] T044 [US3] Activate Mortality tab in `apps/frontend/src/views/poultry/FlockDetailView.vue` (after T042, T043): replace "coming soon" placeholder for `health` tab with `MortalityEventList` + "Log Loss" button (visible for ACTIVE flocks only) that opens `MortalityEventForm`; call `fetchMortalityEvents(id)` on mount and on tab switch to `health`
- [X] T045 [US3] Add mortality i18n keys to `apps/frontend/src/i18n/index.ts` (FR, AR, Darija): `mortality_events_title`, `mortality_log_loss`, `mortality_event_type`, `mortality_event_count`, `mortality_event_date`, `mortality_event_cause`, `mortality_event_empty`, `mortality_type_natural_death`, `mortality_type_cull`, `mortality_cause_disease`, `mortality_cause_injury`, `mortality_cause_predator`, `mortality_cause_low_weight`, `mortality_cause_heat_stress`, `mortality_cause_unknown`, `mortality_cause_other`
- [X] T046 [US3] Write `MortalityServiceTest` in `apps/backend/src/test/java/ma/farmsense/MortalityServiceTest.java` (after T039): 5 tests — create success (headcount decrements), count exceeds headcount (422), flock not ACTIVE (409), transaction atomicity (if save fails, mortality record not persisted), findByFlock returns ordered list
- [ ] T047 [P] [US3] Add Playwright E2E tests for US3 in `apps/frontend/e2e/poultry.spec.ts`: Mortality tab visible in flock detail; "Log Loss" button opens modal for ACTIVE flock; mortality form has type/count/date inputs; cause select has correct options

**Checkpoint**: Log a loss on an active flock → headcount decrements in the same session → event appears in Mortality tab history. Attempting to log more than current headcount shows validation error.

---

## Phase 6: User Story 4 — Flock Status Lifecycle (Priority: P4)

**Goal**: Operator can retire a flock (PHASED_OUT) or mark it sold (SOLD), removing it from the active view while preserving all history.

**Independent Test**: Change a flock's status to PHASED_OUT → verify it disappears from ACTIVE filter → switch to All → flock is visible with all historical data intact.

### Implementation for User Story 4

- [X] T048 [P] [US4] Modify `UpdateFlockRequest` Java record in `apps/backend/src/main/java/ma/farmsense/dto/poultry/UpdateFlockRequest.java` (after T006): ensure `status` field accepts `PHASED_OUT`; add `housingLocationId` (UUID, nullable; no batchCode — read-only after creation)
- [X] T049 [US4] Update `FlockService.delete()` in `apps/backend/src/main/java/ma/farmsense/service/FlockService.java` (after T006): soft-delete now sets status to `PHASED_OUT` (not `FINISHED`)
- [X] T050 [US4] Update `poultry.store.ts` `deleteFlock` action in `apps/frontend/src/stores/poultry.store.ts` (after T015): sets local status to `'PHASED_OUT'` (not `'FINISHED'`) after successful delete API call
- [ ] T051 [P] [US4] Add Playwright E2E tests for US4 in `apps/frontend/e2e/poultry.spec.ts`: PHASED_OUT flock hidden from ACTIVE filter; visible in full (unfiltered) list; historical data accessible on detail page

**Checkpoint**: Retiring a flock via the delete button or status change → flock status is PHASED_OUT → no longer shown in ACTIVE filter → full history preserved.

---

## Phase 7: User Story 5 — Edit Flock Details (Priority: P5)

**Goal**: Operator can update flock name, housing assignment, notes, and status. Initial headcount is read-only after creation.

**Independent Test**: Edit a flock's housing location → verify updated value is saved and shown on the flock card and detail page. Verify initial headcount field is not editable in the edit form.

### Implementation for User Story 5

- [X] T052 [US5] Update `FlockService.update()` in `apps/backend/src/main/java/ma/farmsense/service/FlockService.java` (after T048): handle `housingLocationId` change (resolve new HousingLocation from repository)
- [X] T053 [P] [US5] Verify `FlockForm.vue` edit mode in `apps/frontend/src/components/poultry/FlockForm.vue` (after T028): confirm `batchCode` field is rendered as read-only text (not editable input) in edit mode; confirm `housingLocationId` select is editable; confirm all status options including PHASED_OUT are available in edit mode
- [ ] T054 [P] [US5] Add Playwright E2E test for US5 in `apps/frontend/e2e/poultry.spec.ts`: edit form opens from flock detail; batchCode field is read-only; housing location can be changed; save updates the flock detail view

**Checkpoint**: Edit form correctly distinguishes create vs. edit mode. Batch code is immutable. Housing location and status are editable.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Verification, E2E completeness, and any gaps across all user stories.

- [X] T055 [P] Run backend unit tests with `cd apps/backend && ./mvnw test` and confirm all pass
- [X] T056 [P] Run frontend type-check with `cd apps/frontend && npm run type-check` and confirm zero TS errors in new files
- [X] T057 [P] Run frontend lint with `cd apps/frontend && npm run lint` and confirm no lint errors in new files
- [X] T058 [P] Run Playwright E2E suite with `cd apps/frontend && npx playwright test poultry.spec.ts` and confirm all scenarios pass
- [X] T059 Perform manual quickstart verification per `specs/003-flock-registry/quickstart.md`: create housing location → register flock with batch code → log mortality event → verify headcount decrements → filter by status
- [X] T060 [P] Verify RTL layout for Arabic locale: open app in AR mode, check FlockCard, FlockForm, MortalityEventList render correctly with RTL text direction
- [X] T061 [P] Spot-check 409 batch code conflict: attempt to register two flocks with the same batch code → confirm error shown in form
- [X] T062 [P] Spot-check 422 mortality overflow: attempt to log more deaths than current headcount → confirm error shown in modal
- [X] T063 [P] Spot-check housing location 409: attempt to delete a location that has an active flock → confirm 409 error with explanation

---

## Dependencies & Execution Order

### Phase Dependencies

```text
Phase 1 (Setup)
    └── Phase 2 (Foundational) — BLOCKS everything
            ├── Phase 3 (US1: Register Flock)  🎯 MVP
            ├── Phase 4 (US2: View + Filter)
            ├── Phase 5 (US3: Mortality)
            ├── Phase 6 (US4: Lifecycle Status)
            └── Phase 7 (US5: Edit)
                    └── Phase 8 (Polish)
```

### User Story Dependencies

- **US1 (P1)** — No cross-story deps. Requires Foundational complete.
- **US2 (P2)** — Independent of US1, but benefits from having a flock created (use US1's output). Requires FlockResponse changes from Foundational.
- **US3 (P3)** — Requires US1 to have created at least one ACTIVE flock for manual testing. Backend independent.
- **US4 (P4)** — Partially overlaps with US1 (PHASED_OUT enum). Status change flows through edit (US5), but both are independently testable.
- **US5 (P5)** — Depends on US1 (need an existing flock). Backend is independent.

### Within Each User Story

- DTOs before services
- Services before controllers
- Backend before frontend integration
- Frontend types before store before components
- Components before views

### Parallel Opportunities

Within Phase 2 (Foundational), T005–T009 (all enum creation), T013–T014 (repositories), T015–T016 (frontend) can all run in parallel after T004 (migration) is verified.

Within Phase 3 (US1), T017–T019 (Housing DTOs) and T022–T023 (Flock DTO modifications) can run in parallel; T025–T026 (frontend housing components) can run in parallel with backend work.

---

## Parallel Example: Foundational Phase

```text
# Can run concurrently (all different files):
T005  Extend FlockPurpose enum
T006  Extend FlockStatus enum
T007  Create HousingLocationType enum
T008  Create MortalityType enum
T009  Create MortalityCause enum
T015  Extend frontend types
```

## Parallel Example: User Story 3 (Mortality)

```text
# Can run concurrently:
T036  CreateMortalityEventRequest DTO
T037  MortalityEventResponse DTO
T038  MortalityEventCreateResponse DTO
T042  MortalityEventForm.vue component
T043  MortalityEventList.vue component
```

---

## Implementation Strategy

### MVP First (US1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL — blocks all stories)
3. Complete Phase 3: User Story 1 (Register Flock + Housing Locations)
4. **STOP and VALIDATE**: Create a housing location, register a flock with batch code, verify all data saves correctly
5. Demonstrate MVP to stakeholders

### Incremental Delivery

1. Setup + Foundational → foundation ready
2. US1 → Flock registration with batch code and housing → **Demo 1**
3. US2 → Full flock list with filters and age display → **Demo 2**
4. US3 → Mortality logging with headcount auto-decrement → **Demo 3**
5. US4 → Lifecycle status management → **Demo 4**
6. US5 → Edit flock details → **Demo 5**
7. Polish → All tests green → **Release**

---

## Notes

- Tasks T005–T009 (enums) must compile before entity tasks T010–T012 depend on them
- `ageWeeks` is computed in `FlockResponse.from()` — not stored in DB; always fresh
- `batchCode` is immutable after creation: excluded from `UpdateFlockRequest`, rendered read-only in `FlockForm.vue` edit mode
- Housing location delete is blocked (409) if any ACTIVE flock is assigned; reassign or retire flocks first
- Mortality events only allowed on ACTIVE flocks (409 if SOLD or PHASED_OUT)
- All new i18n keys must be added for all three languages: FR, AR (Darija/MSA)
- Constitution Principle I (Offline-First): `createFlock` and `createMortalityEvent` must go through PouchDB offline queue
