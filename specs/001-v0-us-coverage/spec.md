# Feature Specification: V0 User Story Coverage Audit

**Feature Branch**: `001-v0-us-coverage`
**Created**: 2026-03-10
**Status**: Draft
**Input**: User description: "we need to check all the us developed and the missing us from the v0"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Audit All 26 V0 Stories (Priority: P1)

As a **product owner / engineering lead**, I want to review each of the 26 v0 user stories (US-001 to US-071 across 8 epics) against the current implementation, so that I can know exactly which stories are complete, which are partially done, and which have not been started.

**Why this priority**: Without a full picture of the current state, the team cannot confidently plan what remains to reach a shippable v0, nor estimate effort or commit to timelines.

**Independent Test**: Can be fully tested by running through the v0 story list and verifying that each of the 26 stories has a status (Implemented / Partial / Missing) and that all assigned statuses are justified by observable evidence in the codebase or application.

**Acceptance Scenarios**:

1. **Given** the 26 user stories documented in `docs/USER_STORIES_V0.md`, **When** the audit is complete, **Then** every story has a clearly assigned status and the overall completion rate (%) is calculable.
2. **Given** a story marked "Partial", **When** the audit is reviewed, **Then** the specific acceptance criteria that are missing or untestable are listed explicitly for that story.
3. **Given** a story marked "Implemented", **When** a reviewer checks the audit, **Then** at least one verifiable reference (UI flow, API endpoint, or test) is cited as evidence.

---

### User Story 2 - Identify Gap Priorities (Priority: P2)

As a **developer**, I want to see the missing and partial stories grouped by epic and ordered by priority, so that I can pick up the highest-impact gaps first and move toward a releasable v0.

**Why this priority**: Knowing gaps exist is only useful if the team can act on them in a prioritised order. Grouping by epic helps assign work to the right domain (backend, frontend, firmware).

**Independent Test**: Can be fully tested by verifying that the gap report lists only stories with status "Missing" or "Partial", groups them by epic, and indicates a suggested priority order with rationale.

**Acceptance Scenarios**:

1. **Given** the completed status assessment, **When** the gap report is generated, **Then** all Missing and Partial stories are listed with their epic, missing acceptance criteria, and a suggested priority (P1/P2/P3).
2. **Given** multiple gaps within the same epic, **When** viewing the gap list, **Then** stories are ordered so that foundational stories (e.g. authentication before plant management) appear before dependent ones.

---

### User Story 3 - Track Progress Over Time (Priority: P3)

As a **product owner**, I want to re-run or update the audit as the team ships stories, so that I can track week-by-week progress toward v0 completion and celebrate milestones.

**Why this priority**: A one-time snapshot is valuable, but a living audit enables the team to measure velocity and hold a clear "done" line for v0.

**Independent Test**: Can be fully tested by updating the status of one story from "Missing" to "Implemented" and confirming the overall completion percentage updates accordingly.

**Acceptance Scenarios**:

1. **Given** a story transitions from "Missing" to "Implemented", **When** the audit document is updated, **Then** the overall completion % and per-epic summary reflect the new count.
2. **Given** the audit is reviewed in week 5, **When** compared to the week 1 baseline, **Then** the delta (stories completed in the interval) is visible and accurate.

---

### Edge Cases

- What if a story has some acceptance criteria implemented on the backend but not on the frontend — is it "Partial" or "Missing"? (Assumed: "Partial", with both layers noted separately.)
- What if acceptance criteria are untestable without physical hardware (ESP32) or external services (WhatsApp, Claude API)? (Assumed: mark as "Blocked — needs hardware/environment" and exclude from the completion % until verified on the home lab.)
- What if a story from the v0 list was intentionally descoped? (Assumed: marked "Deferred" with a rationale note, excluded from completion %.)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The audit MUST cover all 26 user stories listed in `docs/USER_STORIES_V0.md` across all 8 epics (US-001 – US-004, US-010 – US-014, US-020 – US-023, US-030 – US-034, US-040 – US-044, US-050 – US-052, US-060 – US-062, US-070 – US-071).
- **FR-002**: Each user story MUST be assigned exactly one status from: **Implemented**, **Partial**, **Missing**, **Deferred**, or **Blocked**.
- **FR-003**: For every story with status "Partial", the audit MUST list which specific acceptance criteria from the original story are not yet met.
- **FR-004**: For every story with status "Implemented", the audit MUST include at least one evidence reference (e.g. API endpoint, UI screen name, or test identifier) and MUST record product owner sign-off (name + date) before the status is considered final.
- **FR-005**: The audit MUST produce a per-epic summary showing: total stories, implemented count, partial count, missing count, blocked count, and completion percentage. Completion % is calculated as Implemented ÷ (Total − Blocked − Deferred) for each epic and overall.
- **FR-006**: The audit MUST produce a prioritised gap list containing only "Missing" and "Partial" stories, ordered by epic sprint priority (as defined in the Story Map Summary in `USER_STORIES_V0.md`) and then by story dependency.
- **FR-007**: The audit document MUST be updatable — changing a story's status must be reflected in the summary counts and percentages without requiring a full re-run.
- **FR-008**: Stories requiring physical hardware (ESP32) or live external services (WhatsApp, Claude Vision API) MUST be clearly flagged so the team knows a specific environment is needed to verify them.
- **FR-009**: The audit document MUST be structured as one dedicated section per user story, each containing: the story's title, a checkbox list of all its original acceptance criteria, the assigned status, and any evidence reference or notes. An executive summary section with per-epic rollup tables MUST appear at the top.

### Key Entities

- **User Story**: Identified by ID (e.g. US-010), belongs to one Epic, has a title, a list of acceptance criteria, and an assigned audit status.
- **Audit Status**: One of Implemented / Partial / Missing / Deferred / Blocked — assigned to each User Story during the review.
- **Gap Item**: A User Story with status Partial or Missing, annotated with the specific unmet acceptance criteria and a suggested remediation priority.
- **Epic Coverage Report**: A per-epic rollup of total stories, status breakdown, and completion percentage.
- **Overall Coverage Report**: A top-level summary of total v0 stories, completion rate (%), gap count, and blocked count.
- **Sign-off Record**: The product owner's name and date of approval, recorded per story within its section when status is set to "Implemented".

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of the 26 v0 user stories have been reviewed and assigned a status — no story is left unclassified.
- **SC-002**: The team can answer "What is our current v0 completion rate?" in under 60 seconds by consulting the audit document.
- **SC-003**: The prioritised gap list enables a developer to identify and begin work on the highest-priority missing story within one working session, without any additional context-gathering.
- **SC-004**: All 8 epics have a per-epic completion percentage visible at a glance, enabling the team to identify which epics are farthest from done.
- **SC-005**: After any story is shipped, updating the audit takes under 5 minutes and the revised completion metrics are immediately accurate.

## Clarifications

### Session 2026-03-10

- Q: Should the audit output be a dense summary table, detailed per-story sections, or a hybrid? → A: Per-story sections — a dedicated section (heading + full criteria checklist) for each of the 26 stories.
- Q: When calculating completion %, how should Blocked stories count? → A: Excluded from denominator — completion = Implemented ÷ (Total − Blocked − Deferred).
- Q: Who can mark a story as "Implemented"? → A: Product owner final sign-off required for each story.

## Assumptions

- The audit is maintained as a markdown document in the `specs/001-v0-us-coverage/` directory, not as an in-app feature.
- "Implemented" means all acceptance criteria for that story are verifiable against the current state of the codebase or running application — not just that backend logic exists in isolation.
- Stories requiring physical ESP32 hardware or live WhatsApp delivery are marked "Blocked" until they can be verified on the home lab described in the v0 Definition of Done.
- Intentionally descoped stories are marked "Deferred" and excluded from the v0 completion percentage denominator.
- "Blocked" stories are also excluded from the completion % denominator until verified on the home lab. The formula is: completion % = Implemented ÷ (Total − Blocked − Deferred).
- Priority order for gap remediation follows the sprint sequence in the USER_STORIES_V0.md Story Map Summary (Week 1 → Week 7).
