# FarmSense v0 — User Stories

> **Version:** 0.1.0  
> **Target:** HomeGrow — indoor & outdoor plant owners  
> **Timeline:** 8 weeks  
> **Definition of Done:** Feature is tested on the home lab (lily, spider plant, basil, rosemary, mint), works offline, and alerts fire in Darija via WhatsApp.

---

## Table of Contents

1. [Epic 1 — Authentication & Onboarding](#epic-1--authentication--onboarding)
2. [Epic 2 — Plant Profiles & Care Schedules](#epic-2--plant-profiles--care-schedules)
3. [Epic 3 — IoT Sensor Dashboard](#epic-3--iot-sensor-dashboard)
4. [Epic 4 — Device Provisioning (Plug & Play)](#epic-4--device-provisioning-plug--play)
5. [Epic 5 — Smart Alerts & Notifications](#epic-5--smart-alerts--notifications)
6. [Epic 6 — AI Plant Diagnosis](#epic-6--ai-plant-diagnosis)
7. [Epic 7 — Offline & PWA Support](#epic-7--offline--pwa-support)
8. [Epic 8 — Multilingual UI (Darija / Arabic / French)](#epic-8--multilingual-ui-darija--arabic--french)

---

## Epic 1 — Authentication & Onboarding

### US-001 · Register with email

**As a** new user,  
**I want to** create an account with my email and a password,  
**So that** my plant data is private and tied to my account.

**Acceptance Criteria:**
- [ ] Form collects: name, email, password, preferred language (AR / FR / Darija)
- [ ] Password must be ≥ 8 characters with at least one number
- [ ] Duplicate email returns a clear error in the user's chosen language
- [ ] On success, user is redirected to the onboarding flow (add first plant)
- [ ] A JWT access token and refresh token are returned and stored securely

**API:** `POST /api/v1/auth/register`

---

### US-002 · Login

**As a** returning user,  
**I want to** log in with my email and password,  
**So that** I can access my plants and sensor data.

**Acceptance Criteria:**
- [ ] Invalid credentials show a friendly error (not a technical stack trace)
- [ ] Successful login redirects to the dashboard
- [ ] Session persists across page refreshes (token stored in localStorage)
- [ ] "Remember me" keeps the user logged in for 30 days

**API:** `POST /api/v1/auth/login`

---

### US-003 · Token refresh

**As a** logged-in user,  
**I want to** stay logged in without being interrupted,  
**So that** I don't lose my work mid-session.

**Acceptance Criteria:**
- [ ] Access token auto-refreshes silently when it expires (15 min TTL)
- [ ] If refresh token is also expired, user is redirected to login with a clear message
- [ ] All API calls include `Authorization: Bearer <token>` header

**API:** `POST /api/v1/auth/refresh`

---

### US-004 · WhatsApp number registration

**As a** user who wants alerts,  
**I want to** register my WhatsApp phone number during onboarding,  
**So that** I receive sensor alerts on the app I already use daily.

**Acceptance Criteria:**
- [ ] Phone number field with Morocco country code (+212) pre-selected
- [ ] Number is validated (format check, not verified via SMS in v0)
- [ ] User can update their number later from profile settings
- [ ] Alert language follows the user's selected language preference

---

## Epic 2 — Plant Profiles & Care Schedules

### US-010 · Add a plant

**As a** plant owner,  
**I want to** create a profile for each of my plants,  
**So that** I can track their health and get personalised care reminders.

**Acceptance Criteria:**
- [ ] Fields: name (custom), species (dropdown with common species), location (indoor/outdoor/balcony), photo (optional upload)
- [ ] On selecting a species, default sensor thresholds are pre-filled (e.g. Basil: soil 60–85%, temp 18–30°C)
- [ ] User can override any threshold
- [ ] Plant appears on the dashboard immediately after creation
- [ ] Maximum 5 plants on free tier

**API:** `POST /api/v1/plants`

**Default species library (v0):**

| Species | Soil min% | Soil max% | Temp min°C | Temp max°C | Light min lux |
|---------|-----------|-----------|------------|------------|---------------|
| Peace Lily | 50 | 80 | 18 | 27 | 500 |
| Spider Plant | 30 | 60 | 15 | 30 | 800 |
| Basil | 60 | 85 | 18 | 30 | 2000 |
| Rosemary | 20 | 40 | 10 | 30 | 3000 |
| Mint | 55 | 80 | 15 | 25 | 1500 |

---

### US-011 · View all plants

**As a** user,  
**I want to** see all my plants on a single dashboard,  
**So that** I can quickly check which ones need attention.

**Acceptance Criteria:**
- [ ] Grid of plant cards showing: name, species, current soil moisture %, current temp, status indicator (green/orange/red)
- [ ] Red status = at least one sensor is outside threshold
- [ ] Orange = approaching threshold (within 10% of limit)
- [ ] Cards link to the individual plant detail view
- [ ] "No plants yet" empty state with a CTA to add first plant

**API:** `GET /api/v1/plants`

---

### US-012 · Edit a plant profile

**As a** user,  
**I want to** update my plant's name, photo, or thresholds,  
**So that** the app stays accurate as my plant's needs change.

**Acceptance Criteria:**
- [ ] All fields editable inline or via an edit form
- [ ] Photo can be replaced or removed
- [ ] Threshold changes take effect immediately for future alerts
- [ ] Changes are saved optimistically in the UI, synced to backend

**API:** `PUT /api/v1/plants/{id}`

---

### US-013 · Delete a plant

**As a** user,  
**I want to** remove a plant that has died or been moved,  
**So that** my dashboard stays clean.

**Acceptance Criteria:**
- [ ] Confirmation dialog before deletion ("Are you sure? This will delete all sensor history.")
- [ ] Soft delete only — data is retained for 90 days in case of accidental deletion
- [ ] Associated device is unlinked (not deleted — can be reassigned to another plant)

**API:** `DELETE /api/v1/plants/{id}`

---

### US-014 · View care schedule

**As a** plant owner,  
**I want to** see a care schedule for each plant,  
**So that** I know when to water, fertilise, and repot.

**Acceptance Criteria:**
- [ ] Schedule auto-generated based on species defaults at plant creation
- [ ] Shows: next watering date, next fertilising date, next repotting date
- [ ] Watering schedule adjusts dynamically based on recent soil sensor readings (if sensor is connected)
- [ ] User can manually mark a task as "done" which resets the timer

---

## Epic 3 — IoT Sensor Dashboard

### US-020 · View live sensor readings

**As a** plant owner with an ESP32 sensor,  
**I want to** see real-time data from my sensors,  
**So that** I know the exact conditions my plant is in right now.

**Acceptance Criteria:**
- [ ] Readings displayed: soil moisture (%), temperature (°C), humidity (%), light (lux)
- [ ] Readings refresh automatically every 60 seconds on the dashboard
- [ ] Last updated timestamp shown ("Updated 3 min ago")
- [ ] If no reading in >30 min, show "Sensor offline" badge
- [ ] Values colour-coded: green (in range), orange (approaching limit), red (out of range)

**API:** `GET /api/v1/readings/{plantId}/latest`

---

### US-021 · View sensor history charts

**As a** user,  
**I want to** see charts of my sensor readings over time,  
**So that** I can spot trends and understand my plant's environment.

**Acceptance Criteria:**
- [ ] Line charts for: soil moisture, temperature, humidity, light — each on a separate chart
- [ ] Time range selector: 24h / 7d / 30d
- [ ] Threshold lines overlaid on each chart (dashed red for min/max)
- [ ] Zoom and pan on desktop; swipe on mobile
- [ ] Charts render within 2 seconds for up to 2,000 data points

**API:** `GET /api/v1/readings/{plantId}/history?hours=24`

---

### US-022 · Multi-sensor support per plant

**As a** user with a large plant or a greenhouse shelf,  
**I want to** assign multiple sensors to a single plant,  
**So that** I can monitor different points (e.g. top soil vs bottom soil).

**Acceptance Criteria:**
- [ ] A plant can have 1–3 assigned devices in v0
- [ ] Each device appears as a labelled tab ("Sensor A", "Sensor B")
- [ ] Alerts fire if *any* sensor breaches a threshold

---

### US-023 · Manual reading entry (no sensor)

**As a** user without an ESP32 sensor yet,  
**I want to** manually enter soil moisture and temperature readings,  
**So that** I can still use the app's tracking and alert features.

**Acceptance Criteria:**
- [ ] Manual entry form available from the plant detail view
- [ ] Fields: soil %, temperature, humidity (all optional)
- [ ] Manual readings appear on the same charts as sensor readings (with a "manual" indicator)
- [ ] App works 100% without any sensor hardware

---

## Epic 4 — Device Provisioning (Plug & Play)

### US-030 · Generate a device setup code

**As a** user who just received an ESP32 sensor kit,  
**I want to** get a setup code from the app,  
**So that** I can link my sensor to my account without writing any code.

**Acceptance Criteria:**
- [ ] "Add a sensor" button in the app generates a 6-character alphanumeric code (e.g. `K7-X9P`)
- [ ] Code expires in 15 minutes
- [ ] Code is displayed with step-by-step visual instructions (connect to FarmSense-Setup WiFi, enter code)
- [ ] A new code can be generated if the first one expires

**API:** `POST /api/v1/devices/setup-code`

---

### US-031 · Provision device via captive portal

**As a** user,  
**I want to** configure my ESP32's WiFi credentials from my phone's browser,  
**So that** the sensor can connect to my home network without me touching any code.

**Acceptance Criteria:**
- [ ] ESP32 with unconfigured WiFi broadcasts `FarmSense-Setup` access point
- [ ] Phone connecting to `FarmSense-Setup` is auto-redirected to the portal (captive portal)
- [ ] Portal page shows: WiFi network selector, password field, setup code field
- [ ] Portal page is in Arabic/Darija by default, with a language toggle
- [ ] On submit, ESP32 saves credentials to flash and reboots into normal mode
- [ ] After reboot, device calls `POST /api/v1/devices/claim` to register itself

---

### US-032 · Claim device to account

**As a** user,  
**I want to** see my sensor appear in the app after I've configured it,  
**So that** I can assign it to a plant and start receiving data.

**Acceptance Criteria:**
- [ ] Device appears in "My Devices" within 60 seconds of successful provisioning
- [ ] Device shows: device ID, online/offline status, last seen timestamp
- [ ] User can assign the device to any of their plants
- [ ] One device can only belong to one plant at a time (but can be reassigned)
- [ ] Setup code becomes invalid after first use (one-time token)

**API:** `POST /api/v1/devices/claim`

---

### US-033 · View device status

**As a** user,  
**I want to** see whether my ESP32 sensor is online and working,  
**So that** I know if my plant monitoring is active.

**Acceptance Criteria:**
- [ ] Device list shows: device ID, assigned plant, status (online/offline), last reading timestamp, battery % (if applicable)
- [ ] Offline device shows how long it has been offline
- [ ] User can rename a device (e.g. "Living Room Lily")
- [ ] User can unlink a device from a plant

**API:** `GET /api/v1/devices`

---

### US-034 · Reset device WiFi

**As a** user who changed their home WiFi password,  
**I want to** reconfigure my sensor without reprogramming it,  
**So that** the sensor reconnects to the new network.

**Acceptance Criteria:**
- [ ] Holding the reset button on the ESP32 for 5 seconds clears saved WiFi credentials from flash
- [ ] Device re-enters provisioning mode (broadcasts `FarmSense-Setup` again)
- [ ] Existing device claim is preserved — user does not need to re-enter the setup code
- [ ] Documented in the hardware quick-start guide

---

## Epic 5 — Smart Alerts & Notifications

### US-040 · Receive WhatsApp alert when soil is dry

**As a** plant owner,  
**I want to** receive a WhatsApp message when my plant's soil drops below the threshold,  
**So that** I water it before it starts wilting.

**Acceptance Criteria:**
- [ ] Alert fires when soil moisture drops below `plant.soil_min` for 2 consecutive readings (30 min)
- [ ] Message in user's preferred language:
  - Darija: `"🌱 Trab jaf! Sqi daba — {plantName} ({soilValue}%)"`
  - French: `"🌱 Sol sec ! Arrosez maintenant — {plantName} ({soilValue}%)"`
  - Arabic: `"🌱 التربة جافة! اسقِ الآن — {plantName} ({soilValue}%)"`
- [ ] Alert does not repeat for the same plant within 4 hours
- [ ] Alert includes a deep link back to the plant detail view

**API:** Triggered by `AlertScheduler` (Spring `@Scheduled`)

---

### US-041 · Receive alert when temperature is too high

**As a** plant owner,  
**I want to** be alerted when the room temperature exceeds my plant's maximum,  
**So that** I can move it away from a heat source before damage occurs.

**Acceptance Criteria:**
- [ ] Alert fires when temperature exceeds `plant.temp_max` for 2 consecutive readings
- [ ] Darija message: `"🌡️ Skhana bzzaf! — {plantName} ({tempValue}°C)"`
- [ ] Same suppression logic: no repeat within 4 hours for the same plant
- [ ] Separate alert for temperature too low (below `plant.temp_min`)

---

### US-042 · Receive alert when light is insufficient

**As a** plant owner,  
**I want to** be notified if my plant isn't getting enough light,  
**So that** I can reposition it or add a grow light.

**Acceptance Criteria:**
- [ ] Alert fires when light (lux) is below `plant.light_min` for 3 consecutive readings (45 min)
- [ ] Only fires during daylight hours (07:00–19:00 in Morocco timezone)
- [ ] Darija message: `"☀️ Ma3ndha3 daw ikafi — {plantName} ({luxValue} lux)"`

---

### US-043 · View alert history

**As a** user,  
**I want to** see a log of all past alerts,  
**So that** I can understand patterns in my plant's environment.

**Acceptance Criteria:**
- [ ] Alert log sorted by most recent first
- [ ] Each entry: timestamp, plant name, alert type, sensor value that triggered it
- [ ] Filter by: plant, alert type, date range
- [ ] Alert can be marked as "acknowledged"

**API:** `GET /api/v1/alerts`

---

### US-044 · Configure alert preferences

**As a** user,  
**I want to** control which alerts I receive and how often,  
**So that** I'm not overwhelmed with notifications.

**Acceptance Criteria:**
- [ ] Toggle each alert type on/off per plant (soil dry, too hot, too cold, low light)
- [ ] Set quiet hours (e.g. no alerts between 23:00 and 07:00)
- [ ] Choose alert channel: WhatsApp only / push notification only / both
- [ ] Default: all alert types enabled, WhatsApp + push, no quiet hours

---

## Epic 6 — AI Plant Diagnosis

### US-050 · Diagnose a plant from a photo

**As a** plant owner who sees something wrong with a leaf,  
**I want to** take a photo and get an AI diagnosis,  
**So that** I know what's wrong and how to fix it without searching the internet.

**Acceptance Criteria:**
- [ ] Camera or photo gallery picker available from the plant detail view
- [ ] Photo is uploaded to the server and sent to Claude Vision API
- [ ] Response includes:
  - Problem name (e.g. "Iron deficiency", "Root rot", "Spider mites")
  - Severity: Low / Medium / High
  - 3-step treatment plan
  - Prevention advice
- [ ] Response is in the user's preferred language (AR / FR / Darija)
- [ ] Response time ≤ 15 seconds
- [ ] Free tier: 3 diagnoses per month; Home Pro: unlimited

**API:** `POST /api/v1/diagnose`

---

### US-051 · View diagnosis history

**As a** user,  
**I want to** see past diagnoses for a plant,  
**So that** I can track whether a treatment worked and spot recurring problems.

**Acceptance Criteria:**
- [ ] Diagnosis history accessible from the plant detail view
- [ ] Each entry shows: date, problem name, severity, treatment steps, photo thumbnail
- [ ] Entries sorted most recent first
- [ ] History retained indefinitely (not deleted when plant is deleted)

**API:** `GET /api/v1/diagnose/{plantId}/history`

---

### US-052 · Share a diagnosis

**As a** user,  
**I want to** share a diagnosis result via WhatsApp,  
**So that** I can ask a friend or agronomist for a second opinion.

**Acceptance Criteria:**
- [ ] "Share" button on the diagnosis result page
- [ ] Share creates a WhatsApp message with: plant name, problem name, photo link, treatment summary
- [ ] Share link is publicly accessible (no login required) for 7 days

---

## Epic 7 — Offline & PWA Support

### US-060 · Install the app on Android

**As a** mobile user,  
**I want to** install FarmSense on my phone home screen,  
**So that** I can open it like a native app without going through the browser.

**Acceptance Criteria:**
- [ ] PWA manifest configured: name, icons, theme colour (FarmSense green #3D6B4F), display: standalone
- [ ] Android Chrome shows "Add to Home Screen" prompt automatically
- [ ] App icon appears on home screen
- [ ] Splash screen shows on app open
- [ ] App opens without address bar (standalone mode)

---

### US-061 · View plant data while offline

**As a** user in an area with poor connectivity,  
**I want to** see my plant data even without internet,  
**So that** the app is useful in the garden or in the countryside.

**Acceptance Criteria:**
- [ ] Last fetched plant list and latest sensor readings are cached via Workbox service worker
- [ ] Offline banner appears at top of screen when offline
- [ ] Cached data is clearly labelled with its age ("Last synced 2 hours ago")
- [ ] Charts show cached data with an "Offline" watermark

---

### US-062 · Queue manual readings while offline

**As a** user without internet,  
**I want to** enter manual sensor readings that sync when I'm back online,  
**So that** I don't lose data while in the field.

**Acceptance Criteria:**
- [ ] Manual readings submitted while offline are stored in IndexedDB (PouchDB)
- [ ] A sync queue indicator shows pending items ("3 readings pending sync")
- [ ] On reconnection, queued readings are posted to the backend automatically
- [ ] Failed syncs are retried up to 3 times with exponential backoff

---

## Epic 8 — Multilingual UI (Darija / Arabic / French)

### US-070 · Switch language

**As a** Moroccan user,  
**I want to** use the app in Darija, Arabic, or French,  
**So that** I can understand everything without needing technical vocabulary.

**Acceptance Criteria:**
- [ ] Language can be set at registration and changed at any time from settings
- [ ] All UI labels, error messages, and alerts adapt immediately to the selected language
- [ ] Arabic and Darija trigger RTL layout (text alignment, icon positions, navigation direction)
- [ ] Language preference is saved to the user profile (not just localStorage)
- [ ] Numbers use Eastern Arabic numerals (٠١٢٣٤٥٦٧٨٩) when Arabic/Darija is selected (optional toggle)

---

### US-071 · Darija plant care vocabulary

**As a** Darija-speaking user,  
**I want to** see care instructions and alerts in the Moroccan dialect I speak at home,  
**So that** I don't have to mentally translate from French or formal Arabic.

**Acceptance Criteria:**
- [ ] All alert messages use the Darija glossary:
  - Trab jaf = Sol sec / Dry soil
  - Sqi daba = Arroser maintenant / Water now
  - Skhana bzzaf = Trop chaud / Too hot
  - Bard bzzaf = Trop froid / Too cold
  - Ma3ndha3 daw = Pas assez de lumière / Not enough light
  - Werqa safra = Feuille jaune / Yellow leaf
- [ ] Plant species names include the common Moroccan name where applicable
- [ ] Care schedule actions use Darija verbs ("Sqi", "Smed", "Beddel pot")

---

## Story Map Summary

| Epic | Stories | Sprint (v0) |
|------|---------|-------------|
| Authentication & Onboarding | US-001 → US-004 | Week 1 |
| Plant Profiles & Care Schedules | US-010 → US-014 | Week 1–2 |
| IoT Sensor Dashboard | US-020 → US-023 | Week 3–4 |
| Device Provisioning | US-030 → US-034 | Week 3–4 |
| Smart Alerts | US-040 → US-044 | Week 5–6 |
| AI Plant Diagnosis | US-050 → US-052 | Week 7 |
| Offline & PWA | US-060 → US-062 | Week 5–6 |
| Multilingual UI | US-070 → US-071 | Week 1 (foundation), Week 5 (complete) |

---

## Acceptance Testing Checklist (Home Lab)

Before marking v0 as shipped, all of the following must pass on the home lab setup:

- [ ] Peace lily triggers soil alert after soil drops below 50%
- [ ] Basil triggers temp alert when moved next to the oven (>30°C)
- [ ] Rosemary triggers light alert when moved to the shaded corner
- [ ] Spider plant shows 7-day chart with visible watering events
- [ ] New ESP32 provisioned in <5 minutes by a non-technical tester
- [ ] AI diagnosis correctly identifies an overwatered basil photo
- [ ] App works offline — shows cached data with "Offline" indicator
- [ ] WhatsApp alert received in Darija within 5 minutes of threshold breach
- [ ] Language switch from FR → Darija triggers RTL layout correctly
- [ ] PWA installs on Android without errors