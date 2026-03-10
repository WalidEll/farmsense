# Quickstart: V0 User Story Coverage Audit

**Branch**: `001-v0-us-coverage` | **Date**: 2026-03-10

This guide explains how to fill in, maintain, and interpret the v0 coverage report.

---

## What is the coverage report?

`specs/001-v0-us-coverage/coverage-report.md` is the living audit document. It tracks whether each of the 26 v0 user stories has been fully implemented, partially implemented, or is missing. The product owner signs off on each story before it is counted as complete.

---

## Files in this feature

| File | Purpose |
|------|---------|
| `spec.md` | What the audit must deliver (requirements, success criteria) |
| `plan.md` | How the audit is structured and maintained |
| `research.md` | Codebase findings that informed initial story statuses |
| `data-model.md` | Schema for all fields in each story section |
| `contracts/story-section-schema.md` | Exact markdown format for each story entry |
| `coverage-report.md` | **The living audit document** (created by `/speckit.implement`) |
| `quickstart.md` | This file |

---

## Roles

| Role | Responsibility |
|------|---------------|
| **Auditor** (developer) | Inspects each story against the codebase; fills in evidence refs, status, and missing criteria |
| **Product Owner** | Reviews each story section; signs off when all criteria are confirmed met |

---

## How to assess a story (auditor)

1. Open `docs/USER_STORIES_V0.md` and find the story.
2. Read each acceptance criterion.
3. For each criterion, check the relevant layer:
   - **Backend**: Look for the controller endpoint, service logic, and Flyway migration.
   - **Frontend**: Look for the Vue component, Pinia store action, and i18n strings.
   - **Firmware**: Look for the relevant function in `apps/esp32/src/main.ino`.
4. Mark each criterion `[✓]` if met, `[ ]` if not.
5. Assign a status:
   - All `[✓]` → `Implemented` (pending PO sign-off)
   - Mix of `[✓]` and `[ ]` → `Partial`
   - All `[ ]` → `Missing`
   - Cannot verify without hardware/live service → `Blocked`
   - Intentionally removed from v0 → `Deferred`
6. Fill in evidence references (required for Implemented or Partial).
7. Fill in `Missing Criteria` section if Partial.
8. Add notes explaining any ambiguous judgement calls.

---

## How to sign off a story (product owner)

1. Read the story section in `coverage-report.md`.
2. Verify that every `[✓]` criterion is supported by the listed evidence references.
3. If satisfied, add your name and the date to the `PO Sign-off` block.
4. Update the executive summary table counts at the top of the file.

A story is only counted as `Implemented` in the completion % once the PO sign-off block is filled.

---

## Completion % formula

```
Completion % = Implemented ÷ (Total − Blocked − Deferred) × 100
```

- `Total` = 26 (fixed)
- `Blocked` and `Deferred` stories are excluded from the denominator.
- Partial and Missing stories count against the numerator (they are not Implemented).

**Example**: If 22 stories are Implemented, 2 are Partial, 1 is Blocked, 1 is Deferred:
```
Completion % = 22 ÷ (26 − 1 − 1) × 100 = 22 ÷ 24 × 100 = 91.7%
```

---

## How to update when a story ships

1. Open `coverage-report.md`.
2. Find the relevant story section.
3. Change `[ ]` to `[✓]` for the newly met criteria.
4. Update `Status` from `Partial` or `Missing` to `Implemented`.
5. Add or update evidence references.
6. Remove the `Missing Criteria` block (if it was Partial).
7. Leave the `PO Sign-off` block blank — the PO must fill it.
8. Update the per-epic and overall summary tables at the top.
9. Update `Last Updated` date in the header.

---

## How to handle Blocked stories

When hardware or a live external service becomes available:
1. Test each blocked criterion against the real environment.
2. Mark criteria `[✓]` or `[ ]` based on actual results.
3. Change status from `Blocked` to the appropriate status (Implemented / Partial / Missing).
4. Remove the `Verification Blocker` line.
5. Add evidence references from the test run.
6. Request PO sign-off if now Implemented.

---

## Stories that require hardware or live services

| US ID | Blocker |
|-------|---------|
| US-004 | WhatsApp API — delivery confirmation |
| US-031 | Physical ESP32 — captive portal |
| US-032 | Physical ESP32 — device claim after provisioning |
| US-034 | Physical ESP32 — WiFi reset button |
| US-040 | WhatsApp API — alert delivery |
| US-041 | WhatsApp API — alert delivery |
| US-042 | WhatsApp API — alert delivery |
| US-050 | Claude Vision API key — diagnosis response |

These stories should be verified during the home lab testing sprint described in `USER_STORIES_V0.md`.
