# API Contract: Breed Catalogue

**Feature**: 002-breed-catalogue | **Date**: 2026-03-15
**Base path**: `/api/v1/breeds`

All endpoints require `Authorization: Bearer <token>` header.

---

## Endpoints

### 1. List Breeds

**GET** `/api/v1/breeds`

Returns paginated breed catalogue. Pre-seeded breeds are always included; custom breeds are scoped to the authenticated user.

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| search | string | No | Full-text search across name/description in all languages |
| category | string | No | Filter by COMMERCIAL, HERITAGE, CUSTOM |
| purpose | string | No | Filter by LAYERS, BROILERS, DUAL_PURPOSE |
| page | integer | No | Page number (default: 0) |
| size | integer | No | Page size (default: 12, max: 50) |

**Response** `200 OK`:

```json
{
  "content": [
    {
      "id": "uuid",
      "name": "Sasso",
      "nameAr": "ساسو",
      "nameEn": "Sasso",
      "category": "COMMERCIAL",
      "purpose": "BROILERS",
      "origin": "France",
      "imageUrl": "/images/breeds/sasso.jpg",
      "isSystem": true,
      "climateSuitability": "Hot/Arid",
      "avgWeightMaleKg": 4.50,
      "avgWeightFemaleKg": 3.20
    }
  ],
  "totalElements": 15,
  "totalPages": 2,
  "number": 0,
  "size": 12
}
```

---

### 2. Get Breed Detail

**GET** `/api/v1/breeds/{id}`

Returns full breed profile including all template data.

**Response** `200 OK`:

```json
{
  "id": "uuid",
  "name": "Sasso",
  "nameAr": "ساسو",
  "nameEn": "Sasso",
  "category": "COMMERCIAL",
  "purpose": "BROILERS",
  "origin": "France",
  "description": "Race rustique à croissance lente...",
  "descriptionAr": "سلالة ريفية بطيئة النمو...",
  "descriptionEn": "Rustic slow-growing breed...",
  "imageUrl": "/images/breeds/sasso.jpg",
  "isSystem": true,
  "climateSuitability": "Hot/Arid",
  "avgWeightMaleKg": 4.50,
  "avgWeightFemaleKg": 3.20,
  "feedingPrograms": [
    {
      "id": "uuid",
      "growthStage": "STARTER",
      "ageStartDays": 0,
      "ageEndDays": 21,
      "feedType": "Starter crumble 21% protein",
      "dailyQuantityGrams": 35.00,
      "feedingFrequency": 3,
      "notes": null,
      "sortOrder": 0
    }
  ],
  "vaccinationSchedule": [
    {
      "id": "uuid",
      "vaccineName": "Newcastle (B1/La Sota)",
      "recommendedAgeDays": 7,
      "dosage": "1 dose",
      "administrationRoute": "EYE_DROP",
      "isMandatory": true,
      "notes": "Repeat at day 21",
      "sortOrder": 0
    }
  ],
  "productionBenchmarks": [
    {
      "id": "uuid",
      "metricType": "DAILY_WEIGHT_GAIN",
      "expectedValue": 35.0000,
      "unit": "g/day",
      "ageStartDays": 0,
      "ageEndDays": 84,
      "notes": null,
      "sortOrder": 0
    }
  ],
  "housingGuidelines": [
    {
      "id": "uuid",
      "parameterName": "SPACE_PER_BIRD",
      "recommendedValue": "0.1",
      "unit": "m²",
      "growthStage": "ALL",
      "notes": "Increase to 0.15 m² in hot climate",
      "sortOrder": 0
    }
  ],
  "createdAt": "2026-03-15T00:00:00Z",
  "updatedAt": "2026-03-15T00:00:00Z"
}
```

**Error** `404 Not Found`: Breed does not exist or is a custom breed owned by another user.

---

### 3. Create Custom Breed

**POST** `/api/v1/breeds`

Creates a farmer-owned custom breed entry.

**Request body**:

```json
{
  "name": "Beldi-Fayoumi Cross",
  "nameAr": "تهجين بلدي-فيومي",
  "nameEn": "Beldi-Fayoumi Cross",
  "category": "CUSTOM",
  "purpose": "DUAL_PURPOSE",
  "origin": "Morocco",
  "description": "Croisement local adapté au climat marocain",
  "descriptionAr": "تهجين محلي متكيف مع المناخ المغربي",
  "descriptionEn": "Local cross adapted to Moroccan climate",
  "imageUrl": null,
  "climateSuitability": "Hot/Arid",
  "avgWeightMaleKg": 3.00,
  "avgWeightFemaleKg": 2.20,
  "feedingPrograms": [],
  "vaccinationSchedule": [],
  "productionBenchmarks": [],
  "housingGuidelines": []
}
```

**Validation**:
- `name`: required, max 255 chars
- `category`: required, must be CUSTOM (farmers cannot create system breeds)
- `purpose`: required, one of LAYERS, BROILERS, DUAL_PURPOSE
- All other fields: optional
- Child arrays: optional, validated individually if provided

**Response** `201 Created`: Full breed detail (same as GET detail).

**Error** `400 Bad Request`: Validation failure.
**Error** `409 Conflict`: Breed with same name already exists for this user.

---

### 4. Update Custom Breed

**PUT** `/api/v1/breeds/{id}`

Updates a farmer-owned custom breed. System breeds cannot be updated.

**Request body**: Same structure as Create (all fields optional for partial update).

**Response** `200 OK`: Full breed detail.

**Error** `403 Forbidden`: Attempting to update a system breed or another user's breed.
**Error** `404 Not Found`: Breed does not exist.

---

### 5. Delete Custom Breed

**DELETE** `/api/v1/breeds/{id}`

Deletes a farmer-owned custom breed. System breeds cannot be deleted.

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| confirm | boolean | No | Required as `true` if breed is assigned to active flocks |

**Response** `204 No Content`: Breed deleted.

**Error** `403 Forbidden`: Attempting to delete a system breed or another user's breed.
**Error** `409 Conflict`: Breed is assigned to active flocks and `confirm=true` not provided. Response includes flock count.

```json
{
  "error": "BREED_IN_USE",
  "message": "This breed is assigned to 3 active flock(s). Pass ?confirm=true to proceed.",
  "activeFlockCount": 3
}
```

---

### 6. Generate Flock Programs from Breed

**GET** `/api/v1/breeds/{id}/templates`

Returns the breed's template data formatted for flock program creation. This is a convenience endpoint that returns the same template data as the detail endpoint but structured for direct use in flock creation forms.

**Response** `200 OK`:

```json
{
  "breedId": "uuid",
  "breedName": "Sasso",
  "feedingPrograms": [...],
  "vaccinationSchedule": [...],
  "productionBenchmarks": [...],
  "housingGuidelines": [...]
}
```

---

## Modified Endpoints

### Flock Create (updated)

**POST** `/api/v1/flocks`

Updated request body adds optional `breedId`:

```json
{
  "name": "Lot Mars 2026",
  "breedId": "uuid",
  "birdCount": 500,
  "purpose": "BROILERS",
  "...": "..."
}
```

When `breedId` is provided, the response includes the auto-generated programs (or a flag indicating templates were applied).

### Flock Update (updated)

**PUT** `/api/v1/flocks/{id}`

When `breedId` changes, response includes `breedChanged: true` to signal the frontend to offer program regeneration.

### Flock Response (updated)

Adds `breedId`, `breedName`, and `breedImageUrl` fields to the existing response.
