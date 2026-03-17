# API Contracts: Flock Registry (003)

**Base URL**: `/api/v1`
**Auth**: All endpoints require `Authorization: Bearer <access_token>`
**Date**: 2026-03-16

---

## Modified Endpoints

### `POST /api/v1/flocks` — Create Flock

**Request body changes**: Add `batchCode` (required) and `housingLocationId` (optional).

```json
{
  "name": "Spring Layer Batch",
  "batchCode": "FL-2026-003",
  "nameAr": "دجاج الربيع",
  "nameEn": "Spring Layer Batch",
  "breedId": "uuid-of-isa-brown",
  "purpose": "LAYERS",
  "birdCount": 500,
  "startDate": "2026-03-01",
  "housingLocationId": "uuid-of-coop-a",
  "supplierId": "uuid-of-supplier",
  "source": "Purchased",
  "notes": "First batch of the year"
}
```

**Validation**:
- `batchCode`: `@NotBlank @Size(max=100)` — must be unique per user (409 Conflict if duplicate)
- `purpose`: must be one of `LAYERS`, `BROILERS`, `DUAL_PURPOSE`, `BREEDERS`

**Response** `201 Created`:

```json
{
  "id": "uuid",
  "name": "Spring Layer Batch",
  "batchCode": "FL-2026-003",
  "nameAr": "...",
  "nameEn": "...",
  "breedId": "uuid",
  "breedName": "ISA Brown",
  "breedImageUrl": "https://...",
  "birdCount": 500,
  "currentBirdCount": 500,
  "ageWeeks": 2,
  "purpose": "LAYERS",
  "status": "ACTIVE",
  "startDate": "2026-03-01",
  "housingLocationId": "uuid",
  "housingLocationName": "Coop A",
  "supplierId": "uuid",
  "supplierName": "Atlas Poultry",
  "source": "Purchased",
  "notes": "...",
  "createdAt": "2026-03-16T10:00:00Z",
  "updatedAt": "2026-03-16T10:00:00Z"
}
```

**Error responses**:
- `400 Bad Request` — validation failures
- `409 Conflict` — `{ "error": "BATCH_CODE_CONFLICT", "message": "Batch code FL-2026-003 already exists" }`

---

### `PUT /api/v1/flocks/{id}` — Update Flock

**Request body changes**: Add `housingLocationId`. Note: `batchCode` is NOT included in the update DTO (read-only after creation).

```json
{
  "name": "Spring Layer Batch – Moved",
  "housingLocationId": "uuid-of-pen-2",
  "status": "PHASED_OUT",
  "notes": "Updated notes"
}
```

**Note on status values**: Accepts `ACTIVE`, `SOLD`, `PHASED_OUT`. `FINISHED` is accepted for legacy compatibility but not recommended for new operations.

---

### `GET /api/v1/flocks` — List Flocks

**Query params changes**:
- `status`: now accepts `ACTIVE`, `SOLD`, `PHASED_OUT`, `FINISHED`
- `purpose`: now accepts `LAYERS`, `BROILERS`, `DUAL_PURPOSE`, `BREEDERS`

**Response** `200 OK`: Array of FlockResponse (same shape as POST response above).

---

### `GET /api/v1/flocks/{id}` — Get Flock

Response now includes `ageWeeks`, `batchCode`, `housingLocationId`, `housingLocationName`.

---

## New Endpoints: Housing Locations

### `GET /api/v1/housing-locations`

List all housing locations for the authenticated user.

**Query params**:
- `type` (optional): `COOP` | `PEN` | `FREE_RANGE`

**Response** `200 OK`:
```json
[
  {
    "id": "uuid",
    "name": "Coop A",
    "nameAr": "حظيرة أ",
    "nameEn": "Coop A",
    "locationType": "COOP",
    "notes": "North side, capacity ~800 birds",
    "currentFlockCount": 2,
    "createdAt": "2026-01-01T00:00:00Z",
    "updatedAt": "2026-01-01T00:00:00Z"
  }
]
```

`currentFlockCount` = number of ACTIVE flocks currently assigned to this location.

---

### `POST /api/v1/housing-locations`

**Request body**:
```json
{
  "name": "Coop A",
  "nameAr": "حظيرة أ",
  "nameEn": "Coop A",
  "locationType": "COOP",
  "notes": "Optional notes"
}
```

**Validation**: `name` `@NotBlank @Size(max=255)`, `locationType` `@NotNull`.

**Response** `201 Created`: Full `HousingLocationResponse`.

---

### `GET /api/v1/housing-locations/{id}`

**Response** `200 OK`: Full `HousingLocationResponse` with flocks currently assigned.

---

### `PUT /api/v1/housing-locations/{id}`

Update name, nameAr, nameEn, locationType, or notes. All fields optional.

**Response** `200 OK`: Updated `HousingLocationResponse`.

---

### `DELETE /api/v1/housing-locations/{id}`

**Business rule**: Cannot delete a location that has one or more ACTIVE flocks assigned to it.

**Response** `204 No Content` on success.
**Error** `409 Conflict` — `{ "error": "LOCATION_HAS_ACTIVE_FLOCKS", "message": "Reassign or retire all active flocks before deleting this location" }`.

---

## New Endpoints: Mortality Events

### `GET /api/v1/flocks/{flockId}/mortality-events`

List all mortality/cull events for a flock, ordered by `mortalityDate` descending.

**Response** `200 OK`:
```json
[
  {
    "id": "uuid",
    "flockId": "uuid",
    "type": "NATURAL_DEATH",
    "count": 5,
    "mortalityDate": "2026-03-14",
    "cause": "DISEASE",
    "notes": "Newcastle disease suspected",
    "createdAt": "2026-03-14T18:30:00Z"
  }
]
```

---

### `POST /api/v1/flocks/{flockId}/mortality-events`

**Request body**:
```json
{
  "type": "NATURAL_DEATH",
  "count": 5,
  "mortalityDate": "2026-03-14",
  "cause": "DISEASE",
  "notes": "Newcastle disease suspected"
}
```

**Validation**:
- `type`: `@NotNull` — `NATURAL_DEATH` or `CULL`
- `count`: `@NotNull @Min(1)`
- `mortalityDate`: `@NotNull` — must not be in the future
- `cause`: optional

**Business rules**:
- `count` must not exceed `flock.currentBirdCount` → `422 Unprocessable Entity` with `{ "error": "EXCEEDS_HEADCOUNT", "message": "Loss of 150 exceeds current headcount of 100" }`
- Flock must be ACTIVE → `409 Conflict` if flock is SOLD or PHASED_OUT

**Response** `201 Created`:
```json
{
  "id": "uuid",
  "flockId": "uuid",
  "type": "NATURAL_DEATH",
  "count": 5,
  "mortalityDate": "2026-03-14",
  "cause": "DISEASE",
  "notes": "...",
  "createdAt": "2026-03-14T18:30:00Z",
  "updatedFlockHeadcount": 495
}
```

`updatedFlockHeadcount` is the new `currentBirdCount` after the event — allows the frontend to update state without a second fetch.

---

## DTO Summary

**New Java records** (all in `dto/` packages, following Records Mandate):

| Record | Package | Purpose |
|--------|---------|---------|
| `CreateHousingLocationRequest` | `poultry.dto` | POST housing-locations body |
| `UpdateHousingLocationRequest` | `poultry.dto` | PUT housing-locations body |
| `HousingLocationResponse` | `poultry.dto` | GET/POST/PUT response |
| `CreateMortalityEventRequest` | `poultry.dto` | POST mortality-events body |
| `MortalityEventResponse` | `poultry.dto` | GET/POST response |

**Modified Java records**:
| Record | Change |
|--------|--------|
| `CreateFlockRequest` | Add `batchCode` (@NotBlank, @Size(max=100)), `housingLocationId` (UUID, nullable) |
| `UpdateFlockRequest` | Add `housingLocationId` (UUID, nullable); no batchCode |
| `FlockResponse` | Add `batchCode`, `housingLocationId`, `housingLocationName`, `ageWeeks` |
