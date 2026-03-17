# Feature Specification: Flock Registry

**Feature Branch**: `003-flock-registry`
**Created**: 2026-03-16
**Status**: Draft

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Register a New Flock (Priority: P1)

A farm operator sets up a new batch of birds by recording the flock's identity, breed, purpose, starting headcount, housing location, and date of acquisition or hatching. This is the core action that makes all subsequent tracking possible.

**Why this priority**: Nothing else in the system (feeding, health, production tracking) can begin until a flock exists. It is the foundational record for every downstream workflow.

**Independent Test**: Can be fully tested by creating a flock and verifying all entered details are saved and displayed correctly. Delivers the minimum viable value of having a named, categorised bird group in the system.

**Acceptance Scenarios**:

1. **Given** no flocks exist, **When** the operator submits a new flock form with name, batch code, breed, purpose, headcount, housing, and start date, **Then** the flock appears in the registry with all entered data and a status of Active.
2. **Given** a flock form is open, **When** the operator submits without providing a name, batch code, or initial headcount, **Then** the system rejects the form and highlights the missing required fields.
3. **Given** a flock form is open, **When** the operator selects a breed from the catalogue (e.g., ISA Brown, Lohmann, Cobb 500), **Then** the breed is associated with the flock record.
4. **Given** a flock form is open, **When** the operator selects the purpose (Layers, Broilers, Dual-Purpose, or Breeders), **Then** the purpose is saved and visible on the flock record.
5. **Given** a flock form is open, **When** the operator enters a batch code that already exists in their account, **Then** the system rejects the form and indicates the batch code must be unique.

---

### User Story 2 — View the Flock List and Individual Flock Details (Priority: P2)

An operator opens the flock registry to see all their flocks at a glance, filter by status or purpose, and drill into a specific flock to see its complete profile including current age, headcount, and housing location.

**Why this priority**: Without visibility into active flocks, the operator cannot manage multiple concurrent batches. Age awareness (derived from start date) is critical for applying the right feeding and health protocols.

**Independent Test**: Can be fully tested by navigating to the flock list after registering at least one flock, filtering by status, and opening the detail page to verify all fields are displayed including calculated age in weeks.

**Acceptance Scenarios**:

1. **Given** multiple flocks exist with different statuses, **When** the operator views the flock list, **Then** all flocks are shown with their name, breed, purpose, current headcount, housing, age in weeks, and status.
2. **Given** the flock list is displayed, **When** the operator filters by "Active", **Then** only flocks with Active status are shown.
3. **Given** the flock list is displayed, **When** the operator filters by purpose (e.g., Layers), **Then** only flocks of that purpose are shown.
4. **Given** a flock exists with a start date, **When** the operator views that flock's detail page, **Then** the system displays the flock's current age in weeks, calculated from the start date.
5. **Given** multiple flocks of different ages, **When** the operator views the list, **Then** each flock shows a distinct age, confirming concurrent multi-batch support.

---

### User Story 3 — Update Flock Headcount via Mortality or Cull Logging (Priority: P3)

An operator records bird losses (deaths or culls) against a specific flock, and the flock's current headcount is automatically reduced to reflect the actual number of live birds.

**Why this priority**: Accurate headcount is essential for calculating feed quantities, production rates, and health ratios. Without automatic adjustment, headcount data drifts from reality.

**Independent Test**: Can be fully tested by recording a mortality event on a flock and verifying the current headcount decreases by the logged number, while the initial headcount remains unchanged.

**Acceptance Scenarios**:

1. **Given** a flock with 500 birds, **When** the operator logs 5 deaths with cause "disease", **Then** the current headcount drops to 495 while the initial headcount remains 500.
2. **Given** a flock with 200 birds, **When** the operator logs 20 culls with cause "low weight", **Then** the current headcount drops to 180.
3. **Given** a flock with 100 birds, **When** the operator attempts to log 150 deaths, **Then** the system rejects the entry because the loss exceeds the current headcount.
4. **Given** a flock, **When** headcount-adjustment events are logged, **Then** a history of individual events (date, count, type, cause, optional notes) is retained for audit purposes.
5. **Given** the event log for a flock, **When** the operator views it, **Then** each entry shows the date, quantity, type (death or cull), and the recorded cause.

---

### User Story 4 — Update Flock Status to Reflect Lifecycle Events (Priority: P4)

An operator marks a flock as Phased Out (retired from production, not sold) or Sold when the batch ends, so the registry accurately reflects which flocks are still active and which have concluded.

**Why this priority**: Lifecycle status management ensures the active flock list remains clean and accurate. It also provides historical data for productivity analysis and planning future batches.

**Independent Test**: Can be fully tested by changing a flock's status to Sold or Phased Out and verifying it no longer appears in the Active-only view but is still accessible in the full history.

**Acceptance Scenarios**:

1. **Given** an Active flock, **When** the operator changes its status to Sold, **Then** the status updates and the flock is excluded from the Active filter.
2. **Given** an Active flock, **When** the operator changes its status to Phased Out, **Then** the status updates and the flock is excluded from the Active filter.
3. **Given** flocks with all statuses, **When** the operator views the full list without filters, **Then** all flocks (Active, Phased Out, Sold) are visible.
4. **Given** a Sold or Phased Out flock, **When** the operator views it, **Then** all historical data (initial headcount, events, dates) is preserved and readable.

---

### User Story 5 — Edit Flock Details (Priority: P5)

An operator corrects or updates a flock's name, housing assignment, notes, or other attributes after initial registration, for example when a flock is moved to a different coop.

**Why this priority**: Errors happen during registration, and birds are physically relocated over time. Keeping flock records accurate is an ongoing operational need.

**Independent Test**: Can be fully tested by editing a flock's housing assignment and verifying the updated value is saved and displayed.

**Acceptance Scenarios**:

1. **Given** a flock with a housing assignment, **When** the operator edits the housing field and saves, **Then** the flock record shows the updated housing location.
2. **Given** a flock, **When** the operator edits the name and saves, **Then** the updated name is reflected immediately across all views.
3. **Given** a flock, **When** the operator edits it, **Then** the initial headcount field is read-only and cannot be changed through the edit form (only adjustable via loss events).

---

### Edge Cases

- What happens when a flock's current headcount reaches zero — does status change automatically to Phased Out, or does the operator change it manually?
- How does the system handle a flock where the start date is today — age should display as "0 weeks" or "Day 1", not an error.
- What if two flocks share the same name — should the system allow duplicates or enforce uniqueness per operator account?
- What if an operator tries to delete a flock that already has associated records (mortality logs, feed records) — the system should prevent hard deletion and require a status change instead.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow operators to create a flock record containing: a display name, a unique batch code, breed (selected from the breed catalogue), purpose, date acquired or hatched, initial headcount, housing assignment, and an optional notes field.
- **FR-001a**: Batch codes MUST be unique per operator account. The system must reject a new flock submission if the batch code already exists for that operator.
- **FR-002**: The system MUST support four flock purposes: Layers, Broilers, Dual-Purpose, and Breeders.
- **FR-003**: The system MUST support three flock lifecycle statuses: Active, Phased Out, and Sold.
- **FR-004**: The system MUST maintain a separate current headcount field that starts equal to the initial headcount and decreases automatically each time a mortality or cull event is recorded against the flock.
- **FR-005**: The system MUST prevent mortality/cull entries that would reduce current headcount below zero.
- **FR-006**: The system MUST calculate and display each flock's current age in weeks, derived from the date acquired or hatched.
- **FR-007**: The system MUST support multiple concurrent flocks per operator account, with no restriction on the number of active flocks at one time.
- **FR-008**: The system MUST allow operators to filter the flock list by status (Active, Phased Out, Sold) and by purpose (Layers, Broilers, Dual-Purpose, Breeders).
- **FR-009**: The system MUST allow operators to update flock details (name, housing, notes, status) after creation. Initial headcount MUST be read-only once the flock is created.
- **FR-010**: The system MUST preserve full flock history (all events, original data) when a flock is moved to Phased Out or Sold status; hard deletion of flocks is not permitted.
- **FR-011**: The system MUST record a history of each headcount-adjustment event with date, quantity, type (natural death or cull), cause (e.g., disease, injury, predator, low weight), and optional free-text notes.
- **FR-012**: The system MUST allow operators to maintain a managed list of housing locations (coops, pens, free-range zones). Each location has a name and type. Operators assign a flock to one location from this managed list; flocks may be reassigned to a different location over time.
- **FR-013**: The system MUST allow operators to view which flocks are currently assigned to each housing location, enabling occupancy tracking across the farm. Multiple active flocks may share one location.

### Key Entities

- **Flock**: The central record grouping a cohort of birds. Holds identity (display name + unique batch code), classification (breed, purpose), lifecycle data (start date, status, initial headcount, current headcount), and housing assignment. One operator may have many flocks.
- **Breed**: A named bird variety selected from a catalogue (ISA Brown, Lohmann, Cobb 500, local breeds, etc.). Each breed has a defined purpose, which pre-fills the flock form as a suggested default.
- **Headcount Event**: A record of a single mortality or cull occurrence against a flock. Captures the date, number of birds lost, type (natural death or cull), cause, and optional free-text notes. The cumulative total of all events determines how far current headcount has dropped from initial.
- **Housing Location**: A named, managed physical space on the farm (coop, pen, free-range zone). Operators create and name their locations once; multiple flocks may be assigned to the same location concurrently.

---

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: An operator can register a new flock — from opening the form to seeing it listed — in under 90 seconds.
- **SC-002**: An operator managing 20 concurrent active flocks can load the full flock list in under 2 seconds.
- **SC-003**: When a mortality or cull event is logged, the current headcount on the flock detail page reflects the updated value within the same session without requiring a manual page refresh.
- **SC-004**: 100% of flock records that have had headcount events retain a complete audit history accessible from the flock detail view.
- **SC-005**: Age in weeks is accurately calculated and displayed for flocks ranging from Day 1 through 104 weeks (2 years), covering the full commercial lifecycle of both broilers and long-running layer flocks.
- **SC-006**: Filtering the flock list by status or purpose returns only matching flocks, with zero incorrect results shown.
- **SC-007**: No flock data (events, headcount history, original record) is lost when a flock status changes to Phased Out or Sold.

---

## Assumptions

- **Breed catalogue is pre-populated**: The system already contains the standard commercial breeds (ISA Brown, Lohmann, Cobb 500, local Moroccan breeds). Operators select from this catalogue; they do not type breed names freely.
- **Single-user flock ownership**: Each flock belongs to exactly one operator account. There is no shared or multi-tenant flock ownership in this version.
- **No restocking model**: This version does not support adding birds to an existing flock after creation (e.g., topping up a flock mid-cycle). Headcount only decreases. A new delivery of birds is registered as a new flock.
- **Age is always in weeks**: Week-based age is the standard unit in commercial poultry management and will be used consistently (not days or months).
- **Phased Out covers all end-of-cycle exits that are not sales**: Includes natural end-of-lay retirement, condemned flocks, and any other non-sale conclusion.
