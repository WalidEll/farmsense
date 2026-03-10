# Data Model: V0 User Story Coverage Audit

**Branch**: `001-v0-us-coverage` | **Date**: 2026-03-10

This document defines the entities and their fields as they appear in the audit document (`coverage-report.md`). This is a document-based model — no database schema is involved.

---

## Entities

### UserStory

Represents one user story from `USER_STORIES_V0.md`.

| Field | Type | Description |
|-------|------|-------------|
| `id` | String | Canonical ID from the spec (e.g. `US-010`) |
| `epic` | String | Parent epic name (e.g. `Epic 2 — Plant Profiles & Care Schedules`) |
| `epicNumber` | Integer | Epic ordinal (1–8) |
| `title` | String | Short title (e.g. `Add a plant`) |
| `acceptanceCriteria` | List\<Criterion\> | Ordered list of criteria from the original spec |
| `status` | AuditStatus | One of: Implemented / Partial / Missing / Deferred / Blocked |
| `missingCriteria` | List\<String\> | Populated only when status = Partial; lists unmet criteria by text |
| `evidenceRefs` | List\<String\> | File path + function/component references (required when Implemented) |
| `verificationBlocker` | String | Describes what external dependency blocks verification (when Blocked) |
| `deferralReason` | String | Explains why story was removed from v0 scope (when Deferred) |
| `poSignOff` | SignOff \| null | Product owner name + date (required when status = Implemented, final) |
| `notes` | String | Free-text notes from reviewer |

---

### Criterion

Represents one acceptance criterion checkbox within a story section.

| Field | Type | Description |
|-------|------|-------------|
| `text` | String | Verbatim criterion text from `USER_STORIES_V0.md` |
| `met` | Boolean | `true` if verifiably implemented, `false` otherwise |
| `layer` | String \| null | Which layer implements this criterion: `backend`, `frontend`, `firmware`, `both`, `external` |

---

### AuditStatus

Enumeration of valid story statuses.

| Value | Meaning | Counted in % denominator? |
|-------|---------|--------------------------|
| `Implemented` | All acceptance criteria met; PO sign-off recorded | Yes (as numerator) |
| `Partial` | Some criteria met, at least one is missing or unverifiable | Yes (as gap) |
| `Missing` | No criteria met; story has not been started | Yes (as gap) |
| `Blocked` | Cannot be verified without specific hardware or live external service | **No** |
| `Deferred` | Intentionally removed from v0 scope | **No** |

---

### SignOff

Records product owner approval of an Implemented story.

| Field | Type | Description |
|-------|------|-------------|
| `approver` | String | Full name of product owner |
| `date` | Date | ISO 8601 date of approval (e.g. `2026-03-15`) |

---

### EpicCoverageReport

A per-epic rollup, displayed in the executive summary at the top of `coverage-report.md`.

| Field | Type | Description |
|-------|------|-------------|
| `epicNumber` | Integer | 1–8 |
| `epicName` | String | Full epic name |
| `total` | Integer | Total stories in this epic |
| `implemented` | Integer | Stories with status = Implemented |
| `partial` | Integer | Stories with status = Partial |
| `missing` | Integer | Stories with status = Missing |
| `blocked` | Integer | Stories with status = Blocked |
| `deferred` | Integer | Stories with status = Deferred |
| `completionPct` | Float | `implemented ÷ (total − blocked − deferred) × 100` |

---

### OverallCoverageReport

Top-level summary, displayed as the first item in `coverage-report.md`.

| Field | Type | Description |
|-------|------|-------------|
| `totalStories` | Integer | Always 26 (fixed v0 scope) |
| `implemented` | Integer | Sum across all epics |
| `partial` | Integer | Sum across all epics |
| `missing` | Integer | Sum across all epics |
| `blocked` | Integer | Sum across all epics |
| `deferred` | Integer | Sum across all epics |
| `completionPct` | Float | `implemented ÷ (totalStories − blocked − deferred) × 100` |
| `lastUpdated` | Date | Date the audit was last modified |
| `auditor` | String | Name of person who performed the review |

---

## State Transitions

```
Missing ──────────────────────────────► Partial
   │                                        │
   │                                        │
   ▼                                        ▼
Missing ──────────────────────────────► Implemented (requires PO sign-off)
   │
   ▼
Deferred  (intentional scope removal — irreversible without PO decision)

Any status ──► Blocked  (when environment dependency discovered)
Blocked    ──► Any status (when environment is available and verified)
```

**Rules**:
- A story transitions from Partial → Implemented only when **all** acceptance criteria are marked `met: true` AND a `poSignOff` record is present.
- A story may not be marked Implemented without evidence references (`evidenceRefs` must be non-empty).
- Deferred stories require a written `deferralReason`.
- Blocked stories require a written `verificationBlocker`.
