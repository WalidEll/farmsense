# Contract: Per-Story Section Schema

**Branch**: `001-v0-us-coverage` | **Date**: 2026-03-10

This document defines the exact markdown structure for each user story section in `coverage-report.md`. Every one of the 26 stories MUST use this schema. Deviations break the per-epic rollup table and PO sign-off workflow.

---

## Story Section Template

```markdown
### US-{ID} · {Title}

**Epic**: {Epic Number} — {Epic Name}
**Status**: `{Implemented | Partial | Missing | Blocked | Deferred}`
**Verification Blocker**: {Description of what external dependency blocks this} *(only when Blocked)*
**Deferral Reason**: {Why removed from v0 scope} *(only when Deferred)*

#### Acceptance Criteria

- [{✓ or space}] {Criterion text verbatim from USER_STORIES_V0.md} · *{backend | frontend | firmware | both | external}*
- [{✓ or space}] {Criterion text} · *{layer}*
...

#### Evidence References

- `{file path}:{function or component name}` — {one-line description}
- `{API endpoint}` — {description}
*(Required when status = Implemented or Partial. Leave as "— None yet" when Missing.)*

#### Missing Criteria *(only when Partial)*

- {Exact text of unmet criterion}
- {Exact text of unmet criterion}

#### Notes *(optional)*

{Free-text reviewer notes. Describe what was found, why a criterion is considered met/unmet, or any nuance.}

#### PO Sign-off *(required to finalise Implemented status)*

- **Approved by**: {Product owner full name}
- **Date**: {YYYY-MM-DD}
*(Leave blank until PO reviews and confirms.)*

---
```

---

## Completed Example (US-001)

```markdown
### US-001 · Register with email

**Epic**: 1 — Authentication & Onboarding
**Status**: `Implemented`

#### Acceptance Criteria

- [✓] Form collects: name, email, password, preferred language (AR / FR / Darija) · *frontend*
- [✓] Password must be ≥ 8 characters with at least one number · *backend*
- [✓] Duplicate email returns a clear error in the user's chosen language · *backend + frontend*
- [✓] On success, user is redirected to the onboarding flow (add first plant) · *frontend*
- [✓] A JWT access token and refresh token are returned and stored securely · *backend + frontend*

#### Evidence References

- `apps/backend/src/main/java/ma/farmsense/controller/AuthController.java:register()` — POST /api/v1/auth/register
- `apps/backend/src/main/java/ma/farmsense/service/AuthService.java:register()` — validation + JWT issuance
- `apps/backend/src/main/resources/db/migration/V1__initial_schema.sql` — users table schema
- `apps/frontend/src/views/RegisterView.vue` — registration form
- `apps/frontend/src/stores/auth.store.ts:register()` — token storage

#### Notes

Password validation (≥8 chars, at least one number) is enforced in AuthService. The "preferred language" field maps to the `lang` enum (FR/AR/EN) — Darija is included as a UI option but stored as EN after V10 migration.

#### PO Sign-off

- **Approved by**: *(pending)*
- **Date**: *(pending)*

---
```

---

## Partial Example (US-052)

```markdown
### US-052 · Share a diagnosis

**Epic**: 6 — AI Plant Diagnosis
**Status**: `Partial`

#### Acceptance Criteria

- [ ] "Share" button on the diagnosis result page · *frontend*
- [ ] Share creates a WhatsApp message with: plant name, problem name, photo link, treatment summary · *frontend + backend*
- [ ] Share link is publicly accessible (no login required) for 7 days · *backend*

#### Evidence References

- `apps/backend/src/main/java/ma/farmsense/controller/DiagnoseController.java` — diagnose() and history() endpoints; full DiagnoseResponse DTO available
- `apps/backend/src/main/java/ma/farmsense/service/DiagnoseService.java` — response includes all fields needed for sharing

#### Missing Criteria

- "Share" button on the diagnosis result page — frontend share UI not confirmed implemented
- Share link publicly accessible for 7 days — no dedicated share/public-link endpoint found (no `POST /api/v1/diagnose/{id}/share` or equivalent)

#### Notes

The backend DiagnoseResponse DTO contains plant name, problem name, photo reference, and treatment. What's missing is a time-limited public share token (server-side) and the frontend "Share" button + WhatsApp deep link.

#### PO Sign-off

- **Approved by**: *(not applicable — status is Partial)*
- **Date**: *(not applicable)*

---
```

---

## Blocked Example (US-031)

```markdown
### US-031 · Provision device via captive portal

**Epic**: 4 — Device Provisioning
**Status**: `Blocked`
**Verification Blocker**: Requires a physical ESP32 DevKit V1 flashed with the current firmware and a physical WiFi network. Cannot be verified in a software-only environment.

#### Acceptance Criteria

- [ ] ESP32 with unconfigured WiFi broadcasts `FarmSense-Setup` access point · *firmware*
- [ ] Phone connecting to `FarmSense-Setup` is auto-redirected to the portal (captive portal) · *firmware*
- [ ] Portal page shows: WiFi network selector, password field, setup code field · *firmware*
- [ ] Portal page is in Arabic/Darija by default, with a language toggle · *firmware*
- [ ] On submit, ESP32 saves credentials to flash and reboots into normal mode · *firmware*
- [ ] After reboot, device calls `POST /api/v1/devices/claim` to register itself · *firmware + backend*

#### Evidence References

- `apps/esp32/src/main.ino:provisionWiFi()` (lines 159–197) — WiFiManager captive portal logic
- `apps/backend/src/main/java/ma/farmsense/controller/DeviceController.java:claim()` — claim endpoint

#### Notes

All firmware logic for the captive portal is present in code. Verification blocked until home lab testing with physical hardware.

#### PO Sign-off

- **Approved by**: *(blocked — cannot sign off until hardware verified)*
- **Date**: *(blocked)*

---
```

---

## Executive Summary Section Template

At the top of `coverage-report.md`, before the story sections, include:

```markdown
# FarmSense v0 — Coverage Report

**Last Updated**: {YYYY-MM-DD}
**Auditor**: {Name}
**Formula**: Completion % = Implemented ÷ (Total − Blocked − Deferred) × 100

## Overall Summary

| Metric | Count |
|--------|-------|
| Total v0 Stories | 26 |
| Implemented | {n} |
| Partial | {n} |
| Missing | {n} |
| Blocked (env needed) | {n} |
| Deferred (out of scope) | {n} |
| **Completion %** | **{n}%** |

## Per-Epic Summary

| Epic | Total | Impl. | Partial | Missing | Blocked | Completion % |
|------|-------|-------|---------|---------|---------|--------------|
| 1 — Auth & Onboarding | 4 | {n} | {n} | {n} | {n} | {n}% |
| 2 — Plant Profiles | 5 | {n} | {n} | {n} | {n} | {n}% |
| 3 — IoT Dashboard | 4 | {n} | {n} | {n} | {n} | {n}% |
| 4 — Device Provisioning | 5 | {n} | {n} | {n} | {n} | {n}% |
| 5 — Smart Alerts | 5 | {n} | {n} | {n} | {n} | {n}% |
| 6 — AI Diagnosis | 3 | {n} | {n} | {n} | {n} | {n}% |
| 7 — Offline & PWA | 3 | {n} | {n} | {n} | {n} | {n}% |
| 8 — Multilingual UI | 2 | {n} | {n} | {n} | {n} | {n}% |
| **Total** | **26** | | | | | |

## Gap List (Partial & Missing — prioritised)

*(Listed in sprint priority order per Story Map Summary)*

| Priority | US ID | Title | Status | Missing / Action |
|----------|-------|-------|--------|-----------------|
| P1 | {US-xxx} | {Title} | Partial | {What needs completing} |
...

---

{Per-story sections follow below}
```
