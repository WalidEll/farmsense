# Implementation Plan: V0 User Story Coverage Audit

**Branch**: `001-v0-us-coverage` | **Date**: 2026-03-10 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `specs/001-v0-us-coverage/spec.md`

## Summary

Create and maintain a living markdown audit document (`coverage-report.md`) that tracks the implementation status of all 26 v0 user stories across 8 epics. Each story gets its own section with a full acceptance-criteria checklist, evidence references, and a product owner sign-off block. An executive summary at the top shows per-epic and overall completion percentages using the formula: `Implemented ÷ (Total − Blocked − Deferred) × 100`. The codebase exploration (research.md) already shows 24 stories are code-level Implemented, 2 are Partial (US-052, US-071), and 0 are Missing.

## Technical Context

**Language/Version**: Markdown (document artifact); no runtime language required
**Primary Dependencies**: `docs/USER_STORIES_V0.md` (source of truth for 26 stories + acceptance criteria); existing codebase in `apps/backend`, `apps/frontend`, `apps/esp32`
**Storage**: File system — `specs/001-v0-us-coverage/coverage-report.md`
**Testing**: Manual review by PO + developer; per-story checklist serves as the test harness
**Target Platform**: Any markdown renderer (GitHub, VS Code, Obsidian)
**Project Type**: Internal audit document
**Performance Goals**: SC-002 — reviewable in under 60 seconds (executive summary at top); SC-005 — story update takes under 5 minutes
**Constraints**: PO sign-off required before Implemented status is final; Blocked/Deferred excluded from % denominator
**Scale/Scope**: 26 user stories, 8 epics, 1 coverage-report.md file, ~600–800 lines of markdown

## Constitution Check

Constitution file is a blank template — no project-specific rules defined. No gates to evaluate. ✓ Pass.

## Project Structure

### Documentation (this feature)

```text
specs/001-v0-us-coverage/
├── plan.md              # This file
├── research.md          # Phase 0: codebase snapshot + decision log
├── data-model.md        # Phase 1: entity schema for audit document
├── quickstart.md        # Phase 1: how to fill and maintain the audit
├── contracts/
│   └── story-section-schema.md   # Phase 1: exact markdown schema per story
├── checklists/
│   └── requirements.md  # Spec quality checklist
└── tasks.md             # Phase 2 output (/speckit.tasks — NOT created here)
```

### Source Code (repository root)

```text
specs/001-v0-us-coverage/
└── coverage-report.md   # The deliverable — living audit document
```

The audit is a pure documentation artifact. No source code in `apps/` is added or modified.

**Structure Decision**: Single document artifact. The audit does not touch any application code — it describes and references existing code. The `coverage-report.md` file lives alongside the other spec artifacts in the feature directory for traceability.

## Phase 0: Research — Complete

**Output**: `research.md`

All unknowns resolved. Key findings:

| Question | Answer |
|----------|--------|
| What's already built? | 24/26 stories Implemented at code level; 2 Partial (US-052, US-071); 0 Missing |
| Evidence reference format | File path + function/component name (e.g. `apps/backend/.../AuthController.java:register()`) |
| US-071 (Darija) classification | Partial — Darija merged into `ar` locale after V10 migration; not independently selectable |
| US-052 (Share diagnosis) classification | Partial — backend DTO ready; share endpoint + frontend UI not confirmed |
| Hardware-blocked stories | US-031, US-032, US-034 (ESP32); US-004, US-040, US-041, US-042 (WhatsApp); US-050 (Claude API) |

**No NEEDS CLARIFICATION items remain.** All items in Technical Context are resolved.

## Phase 1: Design & Contracts — Complete

### Data Model (`data-model.md`)

Entities defined:
- **UserStory**: id, epic, title, acceptanceCriteria, status, missingCriteria, evidenceRefs, verificationBlocker, deferralReason, poSignOff, notes
- **Criterion**: text, met (boolean), layer (backend/frontend/firmware/both/external)
- **AuditStatus**: Implemented / Partial / Missing / Blocked / Deferred (with denominator inclusion rules)
- **SignOff**: approver (string), date (ISO 8601)
- **EpicCoverageReport**: per-epic rollup with completionPct formula
- **OverallCoverageReport**: top-level summary

### Contract (`contracts/story-section-schema.md`)

Defines the exact markdown schema for each story section, with three worked examples:
- **Completed example**: US-001 (Implemented + PO sign-off)
- **Partial example**: US-052 (missing criteria enumerated)
- **Blocked example**: US-031 (hardware blocker noted)

Also defines the Executive Summary section schema (Overall Summary table + Per-Epic table + Gap List).

### Quickstart (`quickstart.md`)

Covers:
- File inventory and purpose of each artifact
- Auditor workflow (how to assess a story, criterion by criterion)
- PO sign-off workflow
- Completion % formula with worked example
- Story update procedure when a story ships
- Blocked story resolution procedure
- Table of hardware/service-blocked stories

## Coverage Report Design

The deliverable `coverage-report.md` will have this structure:

```text
# FarmSense v0 — Coverage Report
  [header: last updated, auditor, formula]

## Overall Summary
  [table: total, implemented, partial, missing, blocked, deferred, completion%]

## Per-Epic Summary
  [table: 8 epics × columns]

## Gap List
  [prioritised table of Partial + Missing stories]

---

## Epic 1 — Authentication & Onboarding
### US-001 · Register with email
  [criteria checklist, evidence refs, PO sign-off]
### US-002 · Login
  ...
### US-003 · Token refresh
  ...
### US-004 · WhatsApp number registration
  ...

## Epic 2 — Plant Profiles & Care Schedules
### US-010 · Add a plant
  ...
[... 8 epics × 26 stories total]
```

**Approximate size**: ~700–900 lines of markdown.

## Preliminary Status (from research.md)

| Epic | Stories | Impl. | Partial | Missing | Blocked |
|------|---------|-------|---------|---------|---------|
| 1 — Auth | 4 | 3 | 0 | 0 | 1 (US-004 WhatsApp) |
| 2 — Plant Profiles | 5 | 5 | 0 | 0 | 0 |
| 3 — IoT Dashboard | 4 | 4 | 0 | 0 | 0 |
| 4 — Device Provisioning | 5 | 2 | 0 | 0 | 3 (US-031, 032, 034 ESP32) |
| 5 — Smart Alerts | 5 | 2 | 0 | 0 | 3 (US-040, 041, 042 WhatsApp) |
| 6 — AI Diagnosis | 3 | 1 | 1 | 0 | 1 (US-050 Claude API) |
| 7 — Offline & PWA | 3 | 3 | 0 | 0 | 0 |
| 8 — Multilingual | 2 | 1 | 1 | 0 | 0 |
| **Total** | **26** | **21** | **2** | **0** | **8\*** |

\* 8 stories have at least one criterion that is Blocked for external verification. Note: stories can be code-level Implemented but still contain Blocked criteria — these are tracked within their section notes, not by changing the whole story status. Only stories where ALL criteria are environment-blocked carry the Blocked status at the story level.

**Revised story-level status**:
- Implemented (all criteria verifiable in code): 21
- Partial (some criteria unverifiable or missing): 2 (US-052, US-071)
- Blocked at story level: 3 (US-031, US-032, US-034 — firmware-only, not testable in software)
- Missing: 0
- Deferred: 0

**Preliminary completion %**: 21 ÷ (26 − 3 − 0) × 100 = **91.3%** (before PO sign-off; 3 Blocked stories excluded from denominator)

## Next Step

Run `/speckit.tasks` to generate the task list for building `coverage-report.md`.
