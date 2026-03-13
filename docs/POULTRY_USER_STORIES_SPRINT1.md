# Poultry Farm Management — Sprint 1 User Stories

> **Module:** Poultry Farm Management
> **Sprint:** 1 — Foundation
> **Date:** 2026-03-13
> **Total endpoints:** 15 (5 per resource)

---

## US-1: Database Schema for Poultry Module

**As a** developer
**I want** the complete poultry database schema deployed via Flyway migration
**So that** all Sprint 1-4 entities have their backing tables ready

### Acceptance Criteria

- [ ] V11 migration creates 10 tables: `suppliers`, `customers`, `flocks`, `expenses`, `incomes`, `vaccination_records`, `mortality_records`, `bird_weights`, `egg_collections`, `feed_records`
- [ ] All tables have UUID PKs with `gen_random_uuid()`, `created_at`/`updated_at` as `TIMESTAMPTZ DEFAULT NOW()`
- [ ] `flocks` has both `bird_count` (initial) and `current_bird_count` (tracks mortality)
- [ ] `flocks.supplier_id` is an optional FK to `suppliers`
- [ ] Financial tables (`expenses`, `incomes`) reference `user_id` (top-level, not flock-nested)
- [ ] Production/health tables reference `flock_id` (required FK)
- [ ] 24 indexes created (on FKs, status columns, date columns)
- [ ] Backend starts with `ddl-auto: validate` without errors
- [ ] 5 backend enums compile: `FlockPurpose`, `FlockStatus`, `ExpenseCategory`, `IncomeProduct`, `FeedType`

### Implementation Notes

**Files to create:**
- `apps/backend/src/main/resources/db/migration/V11__poultry_module.sql`
- `apps/backend/src/main/java/ma/farmsense/entity/FlockPurpose.java`
- `apps/backend/src/main/java/ma/farmsense/entity/FlockStatus.java`
- `apps/backend/src/main/java/ma/farmsense/entity/ExpenseCategory.java`
- `apps/backend/src/main/java/ma/farmsense/entity/IncomeProduct.java`
- `apps/backend/src/main/java/ma/farmsense/entity/FeedType.java`

**Table schema overview:**

| Table | Owner FK | Key columns |
|-------|----------|-------------|
| `suppliers` | `user_id` | name, phone, email, address, products_supplied, notes |
| `customers` | `user_id` | name, phone, email, address, notes |
| `flocks` | `user_id` | supplier_id (optional), name/name_ar/name_en, breed, bird_count, current_bird_count, purpose, status DEFAULT 'ACTIVE', start_date, source, notes |
| `expenses` | `user_id` | flock_id (optional), category, amount DECIMAL(12,2), expense_date, description, supplier_id (optional) |
| `incomes` | `user_id` | flock_id (optional), customer_id (optional), product, quantity, unit_price, total_amount, income_date, description |
| `egg_collections` | `flock_id` | collection_date, total_eggs, broken_eggs DEFAULT 0, notes |
| `feed_records` | `flock_id` | feed_type, quantity_kg, feed_date, cost, notes |
| `bird_weights` | `flock_id` | sample_size, avg_weight_g, weigh_date, notes |
| `mortality_records` | `flock_id` | count, mortality_date, cause, notes |
| `vaccination_records` | `flock_id` | vaccine_name, vaccination_date, next_due_date, administered_by, notes, cost |

**Reference:** `apps/backend/src/main/resources/db/migration/V9__crop_planning.sql`

---

## US-2: Supplier Management

**As a** poultry farmer
**I want** to manage my suppliers (add, view, edit, delete)
**So that** I can track who provides my chicks, feed, vaccines, and equipment

### Acceptance Criteria

- [ ] `POST /api/v1/suppliers` creates a supplier (201) with fields: name (required), phone, email, address, productsSupplied, notes
- [ ] `GET /api/v1/suppliers` returns all suppliers for the authenticated user, ordered by name ASC
- [ ] `GET /api/v1/suppliers?search=term` filters suppliers by name (case-insensitive)
- [ ] `GET /api/v1/suppliers/{id}` returns a single supplier (404 if not owned)
- [ ] `PUT /api/v1/suppliers/{id}` updates supplier fields (partial update, null-check pattern)
- [ ] `DELETE /api/v1/suppliers/{id}` deletes the supplier (204)
- [ ] Frontend: `/poultry/suppliers` page shows a searchable list with create/edit/delete via modal form
- [ ] Supplier form has fields: name*, phone, email, address, productsSupplied, notes
- [ ] All text uses i18n keys (FR/AR/EN)

### Implementation Notes

**Backend files:**
- `apps/backend/src/main/java/ma/farmsense/entity/Supplier.java`
- `apps/backend/src/main/java/ma/farmsense/repository/SupplierRepository.java`
- `apps/backend/src/main/java/ma/farmsense/dto/poultry/CreateSupplierRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/poultry/UpdateSupplierRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/poultry/SupplierResponse.java`
- `apps/backend/src/main/java/ma/farmsense/service/SupplierService.java`
- `apps/backend/src/main/java/ma/farmsense/controller/SupplierController.java`

**Frontend files:**
- `apps/frontend/src/views/poultry/SuppliersView.vue`
- `apps/frontend/src/components/poultry/SupplierForm.vue`

**Entity fields:** id (UUID), user (ManyToOne), name (not null), phone, email, address (TEXT), productsSupplied (TEXT), notes (TEXT), createdAt, updatedAt

**Repository methods:**
- `findByUserOrderByNameAsc(User)`
- `findByUserAndNameContainingIgnoreCaseOrderByNameAsc(User, String)`

**Reference patterns:**
- Entity: `apps/backend/src/main/java/ma/farmsense/entity/CropPlan.java`
- Service: `apps/backend/src/main/java/ma/farmsense/service/CropPlanService.java`
- Controller: `apps/backend/src/main/java/ma/farmsense/controller/CropPlanController.java`
- View: `apps/frontend/src/views/CropPlansView.vue`

---

## US-3: Customer Management

**As a** poultry farmer
**I want** to manage my customers (add, view, edit, delete)
**So that** I can track who buys my eggs, chickens, and manure

### Acceptance Criteria

- [ ] `POST /api/v1/customers` creates a customer (201) with fields: name (required), phone, email, address, notes
- [ ] `GET /api/v1/customers` returns all customers ordered by name ASC
- [ ] `GET /api/v1/customers?search=term` filters by name (case-insensitive)
- [ ] `GET /api/v1/customers/{id}` returns a single customer
- [ ] `PUT /api/v1/customers/{id}` updates customer fields
- [ ] `DELETE /api/v1/customers/{id}` deletes the customer (204)
- [ ] Frontend: `/poultry/customers` page — same pattern as Suppliers
- [ ] Customer form has fields: name*, phone, email, address, notes

### Implementation Notes

**Identical pattern to US-2 (Supplier)** with these differences:
- Entity `Customer.java` has no `productsSupplied` field
- API path: `/api/v1/customers`
- DTOs: `CreateCustomerRequest`, `UpdateCustomerRequest`, `CustomerResponse`

**Backend files:**
- `apps/backend/src/main/java/ma/farmsense/entity/Customer.java`
- `apps/backend/src/main/java/ma/farmsense/repository/CustomerRepository.java`
- `apps/backend/src/main/java/ma/farmsense/dto/poultry/CreateCustomerRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/poultry/UpdateCustomerRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/poultry/CustomerResponse.java`
- `apps/backend/src/main/java/ma/farmsense/service/CustomerService.java`
- `apps/backend/src/main/java/ma/farmsense/controller/CustomerController.java`

**Frontend files:**
- `apps/frontend/src/views/poultry/CustomersView.vue`
- `apps/frontend/src/components/poultry/CustomerForm.vue`

---

## US-4: Flock Management

**As a** poultry farmer
**I want** to manage my flocks (create, view, edit, archive)
**So that** I can track each batch of birds from arrival to sale

### Acceptance Criteria

- [ ] `POST /api/v1/flocks` creates a flock (201) with: name* (+ nameAr, nameEn), breed, birdCount*, purpose* (LAYERS/BROILERS), startDate, supplierId (optional), source, notes
- [ ] On create, `currentBirdCount` is auto-set to `birdCount`
- [ ] `GET /api/v1/flocks` returns all flocks ordered by `createdAt DESC`
- [ ] `GET /api/v1/flocks?status=ACTIVE` filters by status
- [ ] `GET /api/v1/flocks?purpose=LAYERS` filters by purpose
- [ ] `GET /api/v1/flocks/{id}` returns flock detail (including supplier name if linked)
- [ ] `PUT /api/v1/flocks/{id}` updates flock fields
- [ ] `DELETE /api/v1/flocks/{id}` soft-deletes: sets status to `FINISHED` (does not remove from DB)
- [ ] Frontend: `/poultry/flocks` page with filter pills (status + purpose), FlockCard grid, create modal
- [ ] Frontend: `/poultry/flocks/:id` detail page with overview tab (placeholder tabs for Sprint 2-3)
- [ ] FlockCard shows: name, breed, bird count, purpose badge, status badge, start date
- [ ] FlockForm modal: name (FR/AR/EN), breed, birdCount, purpose select, startDate picker, supplier dropdown, notes

### Implementation Notes

**Backend files:**
- `apps/backend/src/main/java/ma/farmsense/entity/Flock.java`
- `apps/backend/src/main/java/ma/farmsense/repository/FlockRepository.java`
- `apps/backend/src/main/java/ma/farmsense/dto/poultry/CreateFlockRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/poultry/UpdateFlockRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/poultry/FlockResponse.java`
- `apps/backend/src/main/java/ma/farmsense/service/FlockService.java`
- `apps/backend/src/main/java/ma/farmsense/controller/FlockController.java`

**Frontend files:**
- `apps/frontend/src/views/poultry/FlocksView.vue`
- `apps/frontend/src/views/poultry/FlockDetailView.vue`
- `apps/frontend/src/components/poultry/FlockCard.vue`
- `apps/frontend/src/components/poultry/FlockForm.vue`

**Entity fields:** id, user (ManyToOne), supplier (ManyToOne, optional), name, nameAr, nameEn, breed, birdCount (Integer, not null), currentBirdCount (Integer, not null), purpose (FlockPurpose, not null), status (FlockStatus, default ACTIVE), startDate (LocalDate), source, notes (TEXT), createdAt, updatedAt

**Repository methods:**
- `findByUserOrderByCreatedAtDesc(User)`
- `findByUserAndStatusOrderByCreatedAtDesc(User, FlockStatus)`
- `findByUserAndPurposeOrderByCreatedAtDesc(User, FlockPurpose)`
- `countByUserAndStatus(User, FlockStatus)` — for dashboard KPIs

**Key service behaviors:**
- `createFlock()` sets `currentBirdCount = birdCount`
- `deleteFlock()` soft-deletes: sets `status = FINISHED`
- If `supplierId` provided on create, resolve via `SupplierRepository`

**Reference patterns:**
- Card: `apps/frontend/src/components/cropplan/PlanCard.vue`
- List view: `apps/frontend/src/views/CropPlansView.vue`
- Detail view: `apps/frontend/src/views/CropPlanDetailView.vue`

---

## US-5: Poultry Navigation & Dashboard Shell

**As a** poultry farmer
**I want** a dedicated Poultry section in the sidebar and a dashboard page
**So that** I can quickly access all poultry features

### Acceptance Criteria

- [ ] Sidebar has a new "Aviculture" section between "Farm Management" and "Operations"
- [ ] Sidebar links: Dashboard (`/poultry`), Flocks (`/poultry/flocks`), Expenses (`/poultry/expenses` — disabled/coming soon), Suppliers (`/poultry/suppliers`), Customers (`/poultry/customers`)
- [ ] Active state highlights correctly for all poultry routes
- [ ] `/poultry` dashboard page shows KPI cards: active flocks count, total birds count
- [ ] Dashboard has quick links to Flocks, Suppliers, Customers pages
- [ ] All 5 routes are registered and lazy-loaded
- [ ] All navigation text uses i18n (FR/AR/EN)

### Implementation Notes

**Modified files:**
- `apps/frontend/src/router/index.ts` — add 5 routes as children of `/` layout
- `apps/frontend/src/components/shared/SideBar.vue` — add Aviculture section
- `apps/frontend/src/i18n/index.ts` — add ~50 keys across FR/AR/EN

**New files:**
- `apps/frontend/src/views/poultry/PoultryDashboardView.vue`

**Routes to add:**

| Path | Name | Component |
|------|------|-----------|
| `poultry` | `poultry-dashboard` | `PoultryDashboardView.vue` |
| `poultry/flocks` | `flocks` | `FlocksView.vue` |
| `poultry/flocks/:id` | `flock-detail` | `FlockDetailView.vue` |
| `poultry/suppliers` | `suppliers` | `SuppliersView.vue` |
| `poultry/customers` | `customers` | `CustomersView.vue` |

**Sidebar section (between Farm Management and Operations):**
- 🐔 Dashboard → `/poultry`
- 🐣 Flocks → `/poultry/flocks`
- 📦 Suppliers → `/poultry/suppliers`
- 👥 Customers → `/poultry/customers`

**i18n key groups:**

| Group | Keys |
|-------|------|
| Navigation | `nav_poultry`, `nav_poultry_dashboard`, `nav_flocks`, `nav_suppliers`, `nav_customers` |
| Flocks | `flocks_title`, `flocks_create`, `flocks_empty`, `flock_name`, `flock_breed`, `flock_bird_count`, `flock_current_birds`, `flock_purpose`, `flock_purpose_layers`, `flock_purpose_broilers`, `flock_status_active`, `flock_status_sold`, `flock_status_finished`, `flock_start_date`, `flock_source`, `flock_supplier` |
| Suppliers | `suppliers_title`, `suppliers_create`, `suppliers_empty`, `supplier_name`, `supplier_phone`, `supplier_email`, `supplier_address`, `supplier_products` |
| Customers | `customers_title`, `customers_create`, `customers_empty`, `customer_name`, `customer_phone`, `customer_email`, `customer_address` |
| Dashboard | `poultry_dashboard_title`, `poultry_active_flocks`, `poultry_total_birds`, `poultry_quick_links` |

**Reference:** `apps/frontend/src/components/shared/SideBar.vue` for nav pattern, `apps/frontend/src/router/index.ts` for route registration

---

## US-6: Frontend Types & Store

**As a** developer
**I want** TypeScript types and a Pinia store for the poultry module
**So that** all views have type-safe data access and state management

### Acceptance Criteria

- [ ] Type aliases added: `FlockPurpose`, `FlockStatus`
- [ ] Interfaces added: `Flock`, `Supplier`, `Customer`
- [ ] Request types added: `CreateFlockRequest`, `UpdateFlockRequest`, `CreateSupplierRequest`, `UpdateSupplierRequest`, `CreateCustomerRequest`, `UpdateCustomerRequest`
- [ ] Pinia store `poultry.store.ts` manages: `flocks`, `currentFlock`, `suppliers`, `customers`, `loading`
- [ ] Store has full CRUD actions for all 3 entities
- [ ] Store uses `api.get/post/put/delete` (NOT `.then(r => r.data)` — the api wrapper already extracts data)
- [ ] `npm run type-check` passes with no errors

### Implementation Notes

**Modified file:** `apps/frontend/src/types/index.ts` — append poultry types at end

**New file:** `apps/frontend/src/stores/poultry.store.ts`

**Store structure:**
- `defineStore('poultry', () => { ... })`
- State: `flocks ref<Flock[]>`, `currentFlock ref<Flock | null>`, `suppliers ref<Supplier[]>`, `customers ref<Customer[]>`, `loading ref(false)`
- Sections: `// ── Flocks ──`, `// ── Suppliers ──`, `// ── Customers ──`
- CRUD for each: fetch, fetchById, create, update, delete

**Critical:** `api.get()` already returns `response.data` — do NOT chain `.then(r => r.data)`.

**Reference:** `apps/frontend/src/stores/cropPlans.store.ts`, `apps/frontend/src/services/api.ts`

---

## Verification Checklist

### Backend
1. `cd apps/backend && JAVA_HOME=/Users/walidelalaouy/Library/Java/JavaVirtualMachines/temurin-17.0.9/Contents/Home ./mvnw compile` — passes
2. Start Docker: `cd apps/backend && docker compose up -d`
3. `./mvnw spring-boot:run` — starts without Hibernate validation errors
4. Smoke test API:
   - `POST /api/v1/suppliers` → 201
   - `POST /api/v1/customers` → 201
   - `POST /api/v1/flocks` → 201, `currentBirdCount == birdCount`
   - `GET /api/v1/flocks?status=ACTIVE` → filtered list
   - `DELETE /api/v1/flocks/{id}` → 204, status changed to FINISHED

### Frontend
5. `cd apps/frontend && npm run build` — passes
6. `npm run type-check` — passes
7. Preview at `http://localhost:5173`:
   - Sidebar shows "Aviculture" section
   - `/poultry` dashboard with KPI cards
   - `/poultry/flocks` list + create modal
   - `/poultry/flocks/:id` detail page
   - `/poultry/suppliers` and `/poultry/customers` CRUD works
   - All text translated (FR/AR/EN)

---

## API Contract Reference

See full OpenAPI 3.0 spec: `apps/backend/src/main/resources/openapi/poultry-sprint1.yaml`
