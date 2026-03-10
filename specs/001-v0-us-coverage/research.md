# Research: V0 User Story Coverage Audit

**Branch**: `001-v0-us-coverage` | **Date**: 2026-03-10
**Source**: Live codebase exploration of `apps/backend`, `apps/frontend`, `apps/esp32`

---

## Decision Log

### Decision 1: Audit execution method

**Decision**: Manual codebase inspection, layer by layer (backend → frontend → firmware), cross-referenced against each acceptance criterion in `USER_STORIES_V0.md`.

**Rationale**: The project has no automated test coverage that maps 1:1 to user story acceptance criteria. Manual inspection is the only reliable method at this stage. Each acceptance criterion is checked against the nearest concrete artifact (controller endpoint, Vue component, Flyway migration, Arduino function).

**Alternatives considered**:
- Automated endpoint scanning: Would only verify backend layer, misses frontend and firmware.
- Counting Flyway migrations: Too coarse-grained — migrations don't map to acceptance criteria.

---

### Decision 2: Evidence reference format per story

**Decision**: Evidence references will cite specific file paths + line ranges or function names. Format: `apps/backend/src/.../AuthController.java:register()` or `apps/frontend/src/views/LoginView.vue`.

**Rationale**: File + function references are stable, navigable in IDEs, and unambiguous for the PO sign-off workflow. API endpoint paths alone (e.g. `POST /api/v1/auth/register`) are valid supplementary references but not sufficient on their own for stories that span multiple layers.

**Alternatives considered**:
- Git commit SHAs: Too opaque for non-developers.
- API endpoint only: Doesn't cover frontend or firmware layers.

---

### Decision 3: Darija / language classification (US-071)

**Decision**: US-071 (Darija vocabulary) is classified **Partial**. Flyway V10 renamed the `DARIJA` enum value to `EN` in the `users.lang` column, and the i18n `index.ts` uses an `ar` locale for both Arabic and Darija. Darija glossary strings are present in the codebase but are not independently selectable by users — they are merged under the `ar` locale.

**Rationale**: The acceptance criteria for US-071 require care schedule actions in Darija verbs ("Sqi", "Smed", "Beddel pot") and plant species with Moroccan names. These strings exist in i18n translations, but the separation between formal Arabic and Darija is not enforced at the locale level after V10.

**Alternatives considered**:
- Marking as Implemented: Unjustified — users cannot explicitly select "Darija" as distinct from Arabic.
- Marking as Missing: Too severe — the vocabulary strings exist and Darija content is served.

---

### Decision 4: US-052 (Share diagnosis) classification

**Decision**: US-052 is classified **Partial**. The backend `DiagnoseResponse` DTO includes all fields needed to construct a share payload. However, no dedicated share endpoint (e.g. `POST /api/v1/diagnose/{id}/share` creating a public short-lived link) was found. The frontend share UI is not confirmed.

**Rationale**: The spec acceptance criteria require: a "Share" button, a WhatsApp-formatted message, and a publicly accessible link valid for 7 days. The data is available but the shareable-link generation and the frontend share button are not confirmed implemented.

---

### Decision 5: Hardware-dependent stories (US-031, US-034) classification

**Decision**: US-031 (Captive portal) and US-034 (WiFi reset) are classified **Implemented** in code but flagged **Blocked** for verification — they cannot be confirmed without physical ESP32 hardware on the home lab.

**Rationale**: `main.ino` contains `provisionWiFi()` (lines 159–197) and `checkResetButton()` (lines 390–406) which implement both flows. The code is present and structured correctly, but end-to-end verification requires the actual hardware + network environment.

---

### Decision 6: WhatsApp-dependent stories classification

**Decision**: US-040, US-041, US-042 (alert stories) and US-004 (WhatsApp registration) are classified **Implemented** in code but flagged **Blocked** for WhatsApp delivery verification — they cannot be end-to-end confirmed without a live Meta WhatsApp Business API token.

**Rationale**: `AlertScheduler` and `WhatsAppService` implement the alert logic. The scheduler, threshold checking, suppression logic, and multilingual messages are all present. Only live delivery confirmation is blocked.

---

## Codebase Snapshot (2026-03-10)

### Backend key files
| Layer | Location |
|-------|----------|
| Controllers (11) | `apps/backend/src/main/java/ma/farmsense/controller/` |
| Services (13) | `apps/backend/src/main/java/ma/farmsense/service/` |
| Entities | `apps/backend/src/main/java/ma/farmsense/entity/` |
| Flyway migrations (V1–V10) | `apps/backend/src/main/resources/db/migration/` |

### Frontend key files
| Layer | Location |
|-------|----------|
| Views (14) | `apps/frontend/src/views/` |
| Stores (6) | `apps/frontend/src/stores/` |
| Components | `apps/frontend/src/components/` (plants/, sensors/, alerts/, devices/, crops/, cropplan/) |
| i18n | `apps/frontend/src/i18n/index.ts` (1,100+ lines, FR/AR/EN) |

### Firmware
| Layer | Location |
|-------|----------|
| Main source | `apps/esp32/src/main.ino` (489 lines) |

---

## Preliminary Coverage Findings

| US ID | Title | Status | Verification Blocker |
|-------|-------|--------|----------------------|
| US-001 | Register | Implemented | — |
| US-002 | Login | Implemented | — |
| US-003 | Token refresh | Implemented | — |
| US-004 | WhatsApp registration | Implemented | WhatsApp delivery (Blocked) |
| US-010 | Add plant | Implemented | — |
| US-011 | View all plants | Implemented | — |
| US-012 | Edit plant | Implemented | — |
| US-013 | Delete plant | Implemented | — |
| US-014 | Care schedule | Implemented | — |
| US-020 | Live readings | Implemented | — |
| US-021 | History charts | Implemented | — |
| US-022 | Multi-sensor | Implemented | — |
| US-023 | Manual entry | Implemented | — |
| US-030 | Setup code | Implemented | — |
| US-031 | Captive portal | Implemented | ESP32 hardware (Blocked) |
| US-032 | Claim device | Implemented | ESP32 hardware (Blocked) |
| US-033 | Device status | Implemented | — |
| US-034 | WiFi reset | Implemented | ESP32 hardware (Blocked) |
| US-040 | Soil dry alert | Implemented | WhatsApp delivery (Blocked) |
| US-041 | Temp alert | Implemented | WhatsApp delivery (Blocked) |
| US-042 | Light alert | Implemented | WhatsApp delivery (Blocked) |
| US-043 | Alert history | Implemented | — |
| US-044 | Alert preferences | Implemented | — |
| US-050 | Diagnose from photo | Implemented | Claude API key (Blocked) |
| US-051 | Diagnosis history | Implemented | — |
| US-052 | Share diagnosis | Partial | Share endpoint + frontend UI missing |
| US-060 | Install on Android | Implemented | — |
| US-061 | View data offline | Implemented | — |
| US-062 | Queue readings offline | Implemented | — |
| US-070 | Switch language | Implemented | — |
| US-071 | Darija vocabulary | Partial | Darija not selectable as distinct locale |

**Preliminary counts (before PO sign-off)**:
- Implemented (code-level): 24
- Partial: 2 (US-052, US-071)
- Missing: 0
- Blocked (need env to verify): 7 stories have at least one criterion that requires hardware or live external service
