# 🏗️ Sprint Plan — Inventory Management Module

**Sprint Duration:** 2 weeks (10 working days)
**Sprint Goal:** Deliver a working MVP of inventory management that allows farmers to add inventory items, record stock movements (purchase, usage, adjustment), track current stock levels, and link inventory usage to farm activities.
**Migration Version:** V13
**Module Prefix:** `/api/v1/inventory`
**Last Updated:** 2026-03-14

---

## 🟢 Sprint Progress Tracker

| Area | Status | Completion |
|------|--------|------------|
| **Frontend Types** (`types/index.ts`) | ✅ Done | 100% |
| **Pinia Store** (`inventory.store.ts`) | ✅ Done | 100% |
| **Router** (3 routes added) | ✅ Done | 100% |
| **Navigation Config** (Inventory group) | ✅ Done | 100% |
| **i18n Keys** (~55 keys × 3 langs) | ✅ Done | 100% |
| **Dashboard View** (`InventoryDashboardView.vue`) | ✅ Done | 100% |
| **Stock Movement Modal** (Log Usage dialog) | ✅ Done | 100% |
| **Build Verification** (`vite build`) | ✅ Passes | 100% |
| **Mobile Responsive** (375px tested) | ✅ Verified | 100% |
| **Backend Migration** (`V13__inventory_module.sql`) | ❌ Not started | 0% |
| **Backend Entities** (Java) | ❌ Not started | 0% |
| **Backend Services** | ❌ Not started | 0% |
| **Backend Controllers** | ❌ Not started | 0% |
| **Backend Unit Tests** | ❌ Not started | 0% |
| **Inventory List View** (separate page) | ⚠️ Partial (reuses dashboard) | 50% |
| **Inventory Item Form** (add/edit) | ❌ Not started | 0% |
| **Item Detail View** | ❌ Not started | 0% |
| **Stock Movements Log View** | ❌ ComingSoon placeholder | 10% |
| **Low-Stock Alert Trigger** | ❌ Not started | 0% |
| **Activity Linking** | ❌ Not started | 0% |

**Overall Sprint Progress: ~35%** (Frontend foundation complete, backend not started)

---

## 📋 Table of Contents

1. [User Stories](#1-user-stories)
2. [Data Model](#2-data-model)
3. [Sprint Backlog](#3-sprint-backlog)
4. [UI Screens](#4-ui-screens)
5. [API Endpoints](#5-api-endpoints)
6. [Deliverables & Timeline](#6-deliverables--timeline)

---

## 1. User Stories

### US-1: Inventory Item Management (8 SP)

**As a** farmer,
**I want to** add, edit, and delete inventory items with details like name, category, unit, and minimum stock level,
**So that** I have a centralized catalog of all supplies on my farm.

**Acceptance Criteria:**
- [ ] Can create an inventory item with: name, category, SKU (optional), unit of measure, current quantity, minimum stock threshold, supplier, storage location, expiry date (optional), notes
- [ ] Categories include: Feed, Seeds, Fertilizers, Pesticides, Veterinary Medicines, Equipment, Packaging, Other
- [ ] Can edit any item field after creation
- [ ] Can delete an item (soft delete — marks as archived)
- [ ] Item names are unique per user
- [ ] Validation: name required, quantity ≥ 0, minimum stock ≥ 0
- [ ] Items are scoped to user/team

---

### US-2: Stock Movement Recording (8 SP)

**As a** farmer,
**I want to** record stock-in (purchases), stock-out (usage), and adjustments for any inventory item,
**So that** I have a full audit trail of how inventory changes over time.

**Acceptance Criteria:**
- [ ] Can record a PURCHASE movement: quantity, unit cost, supplier, reference number, date, notes
- [ ] Can record a USAGE movement: quantity, purpose/activity, linked entity (flock, crop plan, field), date, notes
- [ ] Can record an ADJUSTMENT movement: quantity (+/-), reason (damage, expiry, count correction, loss), date, notes
- [ ] Each movement automatically updates the item's `currentQuantity`
- [ ] Cannot record a USAGE that would make quantity negative (validation error)
- [ ] Movement history is immutable (no edit/delete — only new adjustments)
- [ ] Each movement records the user who created it

---

### US-3: Stock Level Dashboard (5 SP)

**As a** farmer,
**I want to** see an at-a-glance dashboard showing total items, low-stock alerts, recent movements, and stock value,
**So that** I can quickly identify supply issues and make purchasing decisions.

**Acceptance Criteria:**
- [ ] KPI cards: Total Items, Low Stock Items (below threshold), Total Stock Value (sum of qty × last unit cost), Movements This Month
- [ ] Low-stock items list with current qty vs. minimum threshold, sorted by urgency
- [ ] Recent movements list (last 10) with item name, type, quantity, date
- [ ] Category breakdown (pie/doughnut chart) by stock value
- [ ] Date range filter for movement stats

---

### US-4: Inventory List with Filters (5 SP)

**As a** farmer,
**I want to** browse all inventory items with search, category filter, and stock status filter,
**So that** I can quickly find specific items or identify problematic stock levels.

**Acceptance Criteria:**
- [ ] Searchable list/table of all inventory items
- [ ] Filter by: category, stock status (In Stock / Low Stock / Out of Stock), supplier
- [ ] Sort by: name, quantity, category, last updated
- [ ] Each row shows: name, category, current qty, unit, status badge, supplier, last movement date
- [ ] Click to view item detail with full movement history
- [ ] Responsive: table on desktop, cards on mobile

---

### US-5: Link Inventory to Farm Activities (5 SP)

**As a** farmer,
**I want to** link inventory usage to specific farm activities (feeding a flock, treating a crop, planting seeds),
**So that** I can track which resources were consumed by which activity and calculate production costs.

**Acceptance Criteria:**
- [ ] When recording USAGE, can optionally link to: Flock (poultry), Crop Plan (planting), Field, Farm Location
- [ ] Linked activities show their inventory consumption history
- [ ] Usage movements auto-create an EXPENSE transaction in accounting (category = "Inventory", amount = qty × last unit cost)
- [ ] Dashboard shows top-consuming activities

---

### US-6: Low-Stock Notifications (3 SP)

**As a** farmer,
**I want to** be notified when any inventory item drops below its minimum stock threshold,
**So that** I can reorder supplies before running out.

**Acceptance Criteria:**
- [ ] When a movement causes quantity to drop below minimum threshold, create an Alert
- [ ] Alert type: `LOW_STOCK`, severity: `MEDIUM`
- [ ] Alert message includes item name, current quantity, and minimum threshold
- [ ] Alert links to the inventory item detail page
- [ ] Only one active low-stock alert per item (don't create duplicates)

---

### US-7: i18n Support (2 SP) ✅ DONE

**As a** user,
**I want** the inventory module fully translated in French, Arabic, and English,
**So that** I can use the system in my preferred language.

**Acceptance Criteria:**
- [x] All labels, buttons, messages, and empty states translated in FR, AR, EN
- [x] Category names translated
- [x] Movement type labels translated
- [x] Navigation items translated
- [ ] RTL layout works correctly for Arabic

---

**Total Story Points: 36 SP** (target velocity for 2-week sprint)

---

## 2. Data Model

### 2.1 Entity Relationship Diagram

```
┌──────────────────┐       ┌─────────────────────┐
│  inventory_items  │       │  inventory_categories│
├──────────────────┤       ├─────────────────────┤
│ id (PK, UUID)    │       │ id (PK, UUID)       │
│ user_id (FK)     │       │ user_id (FK)        │
│ team_id (FK?)    │       │ name                │
│ category_id (FK) │──────>│ type (enum)         │
│ supplier_id (FK?)│       │ description         │
│ name             │       │ color               │
│ sku              │       │ icon                │
│ unit             │       │ created_at          │
│ current_quantity │       └─────────────────────┘
│ min_stock_level  │
│ last_unit_cost   │       ┌─────────────────────┐
│ storage_location │       │   stock_movements    │
│ expiry_date      │       ├─────────────────────┤
│ notes            │       │ id (PK, UUID)       │
│ status           │       │ item_id (FK)────────│──> inventory_items
│ created_at       │       │ user_id (FK)        │
│ updated_at       │       │ team_id (FK?)       │
│ deleted_at       │       │ type (enum)         │
└──────────────────┘       │ quantity            │
                           │ unit_cost           │
                           │ total_cost          │
                           │ supplier_id (FK?)   │
                           │ reference_number    │
                           │ reason              │
                           │ flock_id (FK?)      │
                           │ crop_plan_id (FK?)  │
                           │ field_id (FK?)      │
                           │ farm_location_id(FK?)│
                           │ transaction_id (FK?)│
                           │ movement_date       │
                           │ notes               │
                           │ quantity_before     │
                           │ quantity_after      │
                           │ created_at          │
                           └─────────────────────┘
```

### 2.2 Enums

| Enum | Values |
|------|--------|
| `InventoryCategoryType` | `FEED`, `SEEDS`, `FERTILIZERS`, `PESTICIDES`, `VETERINARY`, `EQUIPMENT`, `PACKAGING`, `OTHER` |
| `StockMovementType` | `PURCHASE`, `USAGE`, `ADJUSTMENT`, `RETURN`, `TRANSFER` |
| `InventoryUnit` | `KG`, `G`, `L`, `ML`, `UNIT`, `BAG`, `BOX`, `BOTTLE`, `TON`, `DOSE` |
| `InventoryStatus` | `ACTIVE`, `LOW_STOCK`, `OUT_OF_STOCK`, `ARCHIVED` |
| `AdjustmentReason` | `DAMAGE`, `EXPIRY`, `COUNT_CORRECTION`, `LOSS`, `OTHER` |

### 2.3 Migration: `V13__inventory_module.sql`

```sql
-- ============================================
-- V13: Inventory Management Module
-- ============================================

-- 1. Inventory Categories (user-customizable)
CREATE TABLE inventory_categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(20) NOT NULL DEFAULT 'OTHER',
    description     TEXT,
    color           VARCHAR(7) DEFAULT '#6B7280',
    icon            VARCHAR(50),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, name)
);

CREATE INDEX idx_inventory_categories_user ON inventory_categories(user_id);

-- 2. Inventory Items
CREATE TABLE inventory_items (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    team_id           UUID REFERENCES teams(id) ON DELETE SET NULL,
    category_id       UUID REFERENCES inventory_categories(id) ON DELETE SET NULL,
    supplier_id       UUID REFERENCES suppliers(id) ON DELETE SET NULL,
    name              VARCHAR(200) NOT NULL,
    sku               VARCHAR(50),
    unit              VARCHAR(20) NOT NULL DEFAULT 'UNIT',
    current_quantity  DECIMAL(12,2) NOT NULL DEFAULT 0,
    min_stock_level   DECIMAL(12,2) NOT NULL DEFAULT 0,
    last_unit_cost    DECIMAL(12,2) DEFAULT 0,
    storage_location  VARCHAR(200),
    expiry_date       DATE,
    notes             TEXT,
    status            VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at        TIMESTAMPTZ,
    UNIQUE(user_id, name)
);

CREATE INDEX idx_inventory_items_user      ON inventory_items(user_id);
CREATE INDEX idx_inventory_items_team      ON inventory_items(team_id);
CREATE INDEX idx_inventory_items_category  ON inventory_items(category_id);
CREATE INDEX idx_inventory_items_supplier  ON inventory_items(supplier_id);
CREATE INDEX idx_inventory_items_status    ON inventory_items(status);
CREATE INDEX idx_inventory_items_unit      ON inventory_items(unit);

-- 3. Stock Movements (immutable audit log)
CREATE TABLE stock_movements (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id           UUID NOT NULL REFERENCES inventory_items(id) ON DELETE CASCADE,
    user_id           UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    team_id           UUID REFERENCES teams(id) ON DELETE SET NULL,
    type              VARCHAR(20) NOT NULL,
    quantity          DECIMAL(12,2) NOT NULL,
    unit_cost         DECIMAL(12,2),
    total_cost        DECIMAL(12,2),
    supplier_id       UUID REFERENCES suppliers(id) ON DELETE SET NULL,
    reference_number  VARCHAR(100),
    reason            VARCHAR(50),
    flock_id          UUID REFERENCES flocks(id) ON DELETE SET NULL,
    crop_plan_id      UUID REFERENCES crop_plans(id) ON DELETE SET NULL,
    field_id          UUID REFERENCES fields(id) ON DELETE SET NULL,
    farm_location_id  UUID REFERENCES farm_locations(id) ON DELETE SET NULL,
    transaction_id    UUID REFERENCES transactions(id) ON DELETE SET NULL,
    movement_date     DATE NOT NULL DEFAULT CURRENT_DATE,
    notes             TEXT,
    quantity_before   DECIMAL(12,2) NOT NULL,
    quantity_after    DECIMAL(12,2) NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_stock_movements_item          ON stock_movements(item_id);
CREATE INDEX idx_stock_movements_user          ON stock_movements(user_id);
CREATE INDEX idx_stock_movements_team          ON stock_movements(team_id);
CREATE INDEX idx_stock_movements_type          ON stock_movements(type);
CREATE INDEX idx_stock_movements_date          ON stock_movements(movement_date);
CREATE INDEX idx_stock_movements_flock         ON stock_movements(flock_id);
CREATE INDEX idx_stock_movements_crop_plan     ON stock_movements(crop_plan_id);
CREATE INDEX idx_stock_movements_field         ON stock_movements(field_id);
CREATE INDEX idx_stock_movements_transaction   ON stock_movements(transaction_id);

-- 4. Seed default categories for new users (trigger or app-level)
-- Categories will be seeded per-user on first access via application code
```

### 2.4 Java Entities

**InventoryCategory.java**
```java
@Entity @Table(name = "inventory_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryCategory {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private InventoryCategoryType type;

    private String description;

    @Builder.Default
    private String color = "#6B7280";

    private String icon;

    @Column(updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }
}
```

**InventoryItem.java**
```java
@Entity @Table(name = "inventory_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private InventoryCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String name;
    private String sku;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InventoryUnit unit = InventoryUnit.UNIT;

    @Builder.Default
    private BigDecimal currentQuantity = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal minStockLevel = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal lastUnitCost = BigDecimal.ZERO;

    private String storageLocation;
    private LocalDate expiryDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.ACTIVE;

    @Column(updatable = false)
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    @PrePersist
    void onCreate() { createdAt = updatedAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    public boolean isDeleted() { return deletedAt != null; }
}
```

**StockMovement.java**
```java
@Entity @Table(name = "stock_movements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockMovement {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private StockMovementType type;

    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String referenceNumber;
    private String reason;

    // Activity links
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flock_id")
    private Flock flock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_plan_id")
    private CropPlan cropPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private Field field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_location_id")
    private FarmLocation farmLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    private LocalDate movementDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private BigDecimal quantityBefore;
    private BigDecimal quantityAfter;

    @Column(updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }
}
```

---

## 3. Sprint Backlog

### Week 1 — Backend + Core Frontend (Days 1–5)

| # | Task | Story | Points | Owner | Day |
|---|------|-------|--------|-------|-----|
| **T1** | Write `V13__inventory_module.sql` migration | US-1 | 1 | Backend | D1 |
| **T2** | Create enums: `InventoryCategoryType`, `StockMovementType`, `InventoryUnit`, `InventoryStatus`, `AdjustmentReason` | US-1 | 1 | Backend | D1 |
| **T3** | Create entities: `InventoryCategory`, `InventoryItem`, `StockMovement` | US-1 | 2 | Backend | D1 |
| **T4** | Create repositories: `InventoryCategoryRepository`, `InventoryItemRepository`, `StockMovementRepository` | US-1 | 1 | Backend | D1 |
| **T5** | Create DTOs: `CreateItemRequest`, `UpdateItemRequest`, `ItemResponse`, `ItemFilterRequest` | US-1 | 1 | Backend | D2 |
| **T6** | Create DTOs: `CreateMovementRequest`, `MovementResponse`, `DashboardResponse`, `CategoryResponse` | US-2,3 | 1 | Backend | D2 |
| **T7** | Implement `InventoryCategoryService` — CRUD + seed defaults | US-1 | 2 | Backend | D2 |
| **T8** | Implement `InventoryItemService` — CRUD + filters + soft delete | US-1 | 3 | Backend | D2–3 |
| **T9** | Implement `StockMovementService` — record movement + update quantity + validate | US-2 | 3 | Backend | D3 |
| **T10** | Implement dashboard aggregation queries in `InventoryItemService` | US-3 | 2 | Backend | D3 |
| **T11** | Implement low-stock alert trigger in `StockMovementService` | US-6 | 2 | Backend | D4 |
| **T12** | Implement auto-expense transaction creation on USAGE movements | US-5 | 2 | Backend | D4 |
| **T13** | Create `InventoryItemController` — full REST endpoints | US-1,3,4 | 2 | Backend | D4 |
| **T14** | Create `StockMovementController` — movement endpoints | US-2 | 1 | Backend | D4 |
| **T15** | Create `InventoryCategoryController` — category CRUD | US-1 | 1 | Backend | D4 |
| **T16** | Backend unit tests — services + validation | All | 3 | Backend | D5 |
| **T17** | ✅ Add TypeScript types to `types/index.ts` | US-1,2 | 1 | Frontend | D3 |
| **T18** | ✅ Create `inventory.store.ts` Pinia store | US-1,4 | 2 | Frontend | D3 |
| **T19** | ⏳ Create `stockMovements.store.ts` Pinia store (merged into T18) | US-2 | 1 | Frontend | D4 |
| **T20** | ✅ Add routes to `router/index.ts` (3 routes) | US-4 | 1 | Frontend | D4 |
| **T21** | ✅ Add navigation config: "Inventaire" group in sidebar | US-4 | 1 | Frontend | D4 |
| **T22** | ✅ Add i18n keys for inventory module (~55 keys × 3 languages) | US-7 | 2 | Frontend | D5 |

### Week 2 — Frontend Views + Integration (Days 6–10)

| # | Task | Story | Points | Owner | Day |
|---|------|-------|--------|-------|-----|
| **T23** | ✅ Build `InventoryDashboardView` — KPI cards + items table + alerts sidebar + quick restock | US-3 | 3 | Frontend | D6 |
| **T24** | ✅ KPI cards (Feed Stock, Fertilizer, Seeds, Medication) — inline in dashboard | US-3 | 0.5 | Frontend | D6 |
| **T25** | ⏳ Build `StockLevelChart` component (category doughnut) — deferred to v2 | US-3 | 1 | Frontend | — |
| **T26** | ✅ Build `LowStockAlerts` sidebar — in dashboard with severity icons + ORDER NOW | US-3 | 1 | Frontend | D6 |
| **T27** | ⏳ Build `InventoryListView` — separate from dashboard (currently reuses dashboard) | US-4 | 3 | Frontend | D7 |
| **T28** | ✅ Build mobile cards for inventory items — in dashboard view | US-4 | 1 | Frontend | D6 |
| **T29** | ✅ Build category filter tabs (All/Feed/Seeds/Fertilizer/Tools/Medication) — in dashboard | US-4 | 1 | Frontend | D6 |
| **T30** | ❌ Build `InventoryFormView` — add/edit item form | US-1 | 3 | Frontend | D8 |
| **T31** | ❌ Build `CategoryPicker` component (select + create inline) | US-1 | 1 | Frontend | D8 |
| **T32** | ✅ Build `StockMovementDialog` — record purchase/usage/adjustment modal | US-2 | 3 | Frontend | D6 |
| **T33** | ❌ Build `ActivityLinker` component (flock/crop/field selector) | US-5 | 2 | Frontend | D9 |
| **T34** | ❌ Build `MovementHistoryTable` component | US-2 | 1.5 | Frontend | D9 |
| **T35** | ❌ Build `InventoryItemDetailView` — item info + movement history | US-1,2 | 2 | Frontend | D9 |
| **T36** | ❌ Integration testing — full CRUD flow | All | 2 | Full-stack | D10 |
| **T37** | ✅ Mobile responsiveness pass (tested at 375px + 1440px) | All | 1 | Frontend | D6 |
| **T38** | ⏳ Bug fixes + polish | All | 2 | Full-stack | D10 |

---

## 4. UI Screens

### 4.1 Inventory Dashboard (`/inventory`)

```
┌─────────────────────────────────────────────────────────────┐
│  📦 Inventory    Stock Management > Dashboard    [+ New Item]│
├─────────┬──────────┬───────────┬────────────────────────────┤
│ Total   │ Low Stock│ Stock     │ Movements                  │
│ Items   │ Items    │ Value     │ This Month                 │
│  47     │  5 ⚠️   │ 24,500 MAD│  128                       │
├─────────┴──────────┴───────────┴────────────────────────────┤
│                                                             │
│  ┌─── Stock by Category ───┐  ┌─── Low Stock Alerts ──────┐│
│  │   [Doughnut Chart]      │  │ ⚠ Poulet Feed    12/50 kg ││
│  │   Feed: 45%             │  │ ⚠ NPK Fertilizer  3/20 kg ││
│  │   Seeds: 20%            │  │ ⚠ Tomato Seeds   0.5/5 kg ││
│  │   Fertilizers: 15%      │  │ ⚠ Vaccines       2/10 dose││
│  │   Other: 20%            │  │ ⚠ Plastic Bags   50/200   ││
│  └─────────────────────────┘  └────────────────────────────┘│
│                                                             │
│  ┌─── Recent Movements ────────────────────────────────────┐│
│  │ DATE    │ ITEM          │ TYPE     │ QTY  │ BY          ││
│  │ 14 Mar  │ Poulet Feed   │ USAGE    │ -25  │ Walid       ││
│  │ 14 Mar  │ NPK Fert.     │ PURCHASE │ +100 │ Walid       ││
│  │ 13 Mar  │ Corn Seeds    │ USAGE    │ -10  │ Ahmed       ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

### 4.2 Inventory List (`/inventory/items`)

```
┌─────────────────────────────────────────────────────────────┐
│  📦 Inventory Items                          [+ Add Item]   │
├─────────────────────────────────────────────────────────────┤
│  🔍 Search items...                                         │
│  [All Categories ▼] [All Status ▼] [All Suppliers ▼]       │
├─────────────────────────────────────────────────────────────┤
│  NAME          │ CATEGORY    │ QUANTITY │ UNIT │ STATUS     │
│  ─────────────────────────────────────────────────────────  │
│  Poulet Feed   │ 🐔 Feed     │ 12      │ kg   │ ⚠ LOW     │
│  NPK 15-15-15  │ 🌱 Fert.    │ 3       │ kg   │ ⚠ LOW     │
│  Corn Seeds    │ 🌾 Seeds    │ 45      │ kg   │ ✅ OK      │
│  Ivermectin    │ 💊 Vet.     │ 8       │ dose │ ✅ OK      │
│  Plastic Bags  │ 📦 Pkg.     │ 50      │ unit │ ⚠ LOW     │
│  Sprayer XR200 │ 🔧 Equip.   │ 2       │ unit │ ✅ OK      │
├─────────────────────────────────────────────────────────────┤
│  Showing 6 of 47 items           [< 1 2 3 4 5 >]          │
└─────────────────────────────────────────────────────────────┘
```

### 4.3 Add/Edit Item Form (`/inventory/items/new`, `/inventory/items/:id/edit`)

```
┌─────────────────────────────────────────────────────────────┐
│  📦 New Inventory Item                   [Cancel] [Save]    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─── Basic Information ───────────────────────────────────┐│
│  │ Item Name *         [________________________]          ││
│  │ SKU (optional)      [____________]                      ││
│  │ Category *          [Feed              ▼] [+ New]       ││
│  │ Unit of Measure *   [KG ▼]                              ││
│  └─────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌─── Stock Settings ─────────────────────────────────────┐│
│  │ Initial Quantity    [_____0____]                        ││
│  │ Min. Stock Level    [_____0____]                        ││
│  │ Unit Cost (MAD)     [_____0____]                        ││
│  └─────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌─── Details ─────────────────────────────────────────────┐│
│  │ Supplier            [Select supplier...     ▼]          ││
│  │ Storage Location    [________________________]          ││
│  │ Expiry Date         [____/____/________] 📅             ││
│  │ Notes               [________________________]          ││
│  │                     [________________________]          ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

### 4.4 Stock Movement Dialog (Modal)

```
┌─────────────────────────────────────────────────┐
│  Record Stock Movement                     [✕]  │
├─────────────────────────────────────────────────┤
│                                                 │
│  Movement Type                                  │
│  [🛒 Purchase] [📤 Usage] [📋 Adjustment]       │
│                                                 │
│  ── Purchase Details ──                         │
│  Quantity *        [________]  kg               │
│  Unit Cost (MAD)   [________]                   │
│  Supplier          [Select... ▼]                │
│  Reference #       [________]                   │
│  Date              [14/03/2026] 📅              │
│                                                 │
│  ── Link to Activity (optional) ──              │
│  Activity Type     [Flock ▼]                    │
│  Select            [Flock A - Pondeuses ▼]      │
│                                                 │
│  Notes             [________________________]   │
│                                                 │
│  Current Stock: 12 kg → New: 62 kg              │
│                                                 │
│           [Cancel]  [✓ Record Movement]         │
└─────────────────────────────────────────────────┘
```

### 4.5 Item Detail View (`/inventory/items/:id`)

```
┌─────────────────────────────────────────────────────────────┐
│  ← Back    Poulet Feed                [Edit] [Record Move]  │
├─────────────────────────────────────────────────────────────┤
│  Category: 🐔 Feed    Unit: kg    Supplier: Agri-Supply     │
│  SKU: FEED-001        Location: Entrepôt A                  │
│  Expiry: 15/06/2026   Status: ⚠ LOW STOCK                   │
├──────────┬──────────┬───────────┬───────────────────────────┤
│ Current  │ Min.     │ Last Cost │ Total Value               │
│ 12 kg    │ 50 kg    │ 8.5 MAD  │ 102 MAD                   │
├──────────┴──────────┴───────────┴───────────────────────────┤
│                                                             │
│  ┌─── Stock Movement History ──────────────────────────────┐│
│  │ DATE    │ TYPE     │ QTY   │ BEFORE│ AFTER │ LINKED TO  ││
│  │ 14 Mar  │ USAGE    │ -25   │ 37    │ 12    │ Flock A    ││
│  │ 12 Mar  │ PURCHASE │ +50   │ -13   │ 37    │ —          ││
│  │ 10 Mar  │ USAGE    │ -30   │ 17    │ -13   │ Flock B    ││
│  │ 05 Mar  │ PURCHASE │ +100  │ -83   │ 17    │ —          ││
│  │ 01 Mar  │ ADJUST   │ -3    │ -80   │ -83   │ Damage     ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

---

## 5. API Endpoints

### 5.1 Inventory Items (`/api/v1/inventory/items`)

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|-------------|----------|
| `GET` | `/items` | List items with filters | Query: `search`, `categoryId`, `status`, `supplierId`, `page`, `size` | `ItemResponse[]` |
| `POST` | `/items` | Create item | `CreateItemRequest` | `ItemResponse` (201) |
| `GET` | `/items/{id}` | Get item detail | — | `ItemResponse` |
| `PUT` | `/items/{id}` | Update item | `UpdateItemRequest` | `ItemResponse` |
| `DELETE` | `/items/{id}` | Soft-delete item | — | 204 |
| `GET` | `/items/dashboard` | Dashboard KPIs + charts | Query: `from`, `to` | `InventoryDashboardResponse` |
| `GET` | `/items/low-stock` | Low-stock items only | — | `ItemResponse[]` |

### 5.2 Stock Movements (`/api/v1/inventory/movements`)

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|-------------|----------|
| `GET` | `/movements` | List movements | Query: `itemId`, `type`, `from`, `to`, `page`, `size` | `MovementResponse[]` |
| `POST` | `/movements` | Record movement | `CreateMovementRequest` | `MovementResponse` (201) |
| `GET` | `/movements/{id}` | Get movement detail | — | `MovementResponse` |
| `GET` | `/movements/by-item/{itemId}` | Movements for one item | Query: `page`, `size` | `MovementResponse[]` |

### 5.3 Inventory Categories (`/api/v1/inventory/categories`)

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|-------------|----------|
| `GET` | `/categories` | List categories | — | `CategoryResponse[]` |
| `POST` | `/categories` | Create category | `CreateCategoryRequest` | `CategoryResponse` (201) |
| `PUT` | `/categories/{id}` | Update category | `UpdateCategoryRequest` | `CategoryResponse` |
| `DELETE` | `/categories/{id}` | Delete category | — | 204 |

**Total: 14 endpoints**

---

## 6. Deliverables & Timeline

### Sprint Calendar

```
WEEK 1: Backend + Foundation
─────────────────────────────────────────────────
Day 1 (Mon)  │ T1-T4: Migration, Enums, Entities, Repos
Day 2 (Tue)  │ T5-T8: DTOs, CategoryService, ItemService
Day 3 (Wed)  │ T9-T10, T17-T18: MovementService, Dashboard queries, FE types + store
Day 4 (Thu)  │ T11-T15, T19-T21: Alert trigger, Controllers, FE stores + routes
Day 5 (Fri)  │ T16, T22: Backend tests, i18n keys
             │ 🔍 Sprint Review: Backend API smoke test

WEEK 2: Frontend Views + Polish
─────────────────────────────────────────────────
Day 6 (Mon)  │ T23-T26: Inventory Dashboard View + components
Day 7 (Tue)  │ T27-T29: Inventory List View + filters
Day 8 (Wed)  │ T30-T31, T32 start: Item Form + CategoryPicker + Movement Dialog
Day 9 (Thu)  │ T32 finish, T33-T35: Movement Dialog, ActivityLinker, Detail View
Day 10 (Fri) │ T36-T38: Integration test, mobile pass, bug fixes
             │ 🔍 Sprint Review: Full demo
```

### Definition of Done

- [ ] All 14 API endpoints return correct responses
- [ ] Flyway migration runs without errors
- [ ] `./mvnw compile` passes
- [x] `npm run build` passes (verified: 2.44s, 51 precache entries)
- [ ] All 7 user stories meet acceptance criteria
- [x] i18n complete for FR, AR, EN (~55 keys × 3 languages)
- [x] Mobile responsive (tested at 375px + 1440px)
- [ ] Low-stock alerts fire correctly
- [ ] USAGE movements auto-create expense transactions

### Expected Deliverables at Sprint End

| Deliverable | Count | Status |
|-------------|-------|--------|
| Database migration | 1 file | ❌ |
| Java Enums | 5 files | ❌ |
| Java Entities | 3 files | ❌ |
| Java Repositories | 3 files | ❌ |
| Java DTOs | ~12 files | ❌ |
| Java Services | 3 files | ❌ |
| Java Controllers | 3 files | ❌ |
| Frontend Types | 1 file (additions) | ✅ |
| Pinia Stores | 1 file (`inventory.store.ts`) | ✅ |
| Vue Views | 1 of 4 files | ✅ Dashboard |
| Vue Components | 0 of 8 files (inline in dashboard) | ⚠️ |
| Router additions | 1 file (3 routes) | ✅ |
| Nav config additions | 1 file (new group, 3 items) | ✅ |
| i18n additions | 1 file (~55 keys × 3 langs) | ✅ |
| **Total new files** | **~45** | |
| **Completed so far** | **3 new + 4 modified** | **~35%** |

### Navigation Structure (Implemented ✅)

```
📦 Inventaire               ← Sidebar group (nav.group.inventory)
  ├── 📊 Tableau de bord    → /inventory              ✅ InventoryDashboardView
  ├── 📋 Articles en stock  → /inventory/items         ✅ InventoryDashboardView (shared)
  └── 📈 Mouvements de stock → /inventory/movements    ⏳ ComingSoonView
```

### Risk Register

| Risk | Impact | Mitigation |
|------|--------|------------|
| Activity linking complexity (US-5) | Medium | Decouple: make linking optional, use simple FK selectors |
| Auto-expense creation (US-5) | Medium | Reuse existing TransactionService.create() pattern |
| Category seeding per user | Low | Seed on first API call to `/categories` if empty |
| Migration conflicts with V12 | Low | Test migration chain V1→V13 on clean DB |

---

## 7. Implementation Order — Step-by-Step Dev Guide

> **Read this before writing any code.**
> Each step builds on the previous one. Do NOT skip steps.
> Each step ends with a verification command to confirm it works before moving on.

---

### 🔴 PHASE 1 — Database & Entities (Day 1)

**Step 1: Flyway Migration** `V13__inventory_module.sql`
```
📁 apps/backend/src/main/resources/db/migration/V13__inventory_module.sql
```
- Creates 3 tables: `inventory_categories`, `inventory_items`, `stock_movements`
- SQL is already written in Section 2.3 of this document — copy it exactly
- **Verify:** `docker compose up -d && ./mvnw flyway:migrate` — no errors

**Step 2: Java Enums** (5 files)
```
📁 ma/farmsense/entity/
  ├── InventoryCategoryType.java   → FEED, SEEDS, FERTILIZER, PESTICIDE, VETERINARY, EQUIPMENT, PACKAGING, OTHER
  ├── StockMovementType.java       → PURCHASE, USAGE, ADJUSTMENT, RETURN, TRANSFER
  ├── InventoryUnit.java           → KG, G, L, ML, UNIT, BAG, BOX, BOTTLE, TON, DOSE
  ├── InventoryStatus.java         → ACTIVE, LOW_STOCK, OUT_OF_STOCK, ARCHIVED
  └── AdjustmentReason.java        → DAMAGE, EXPIRY, COUNT_CORRECTION, LOSS, OTHER
```
- **Pattern:** copy `CropCategory.java` enum style
- **Verify:** `./mvnw compile` — no errors

**Step 3: JPA Entities** (3 files)
```
📁 ma/farmsense/entity/
  ├── InventoryCategory.java   → see Section 2.4
  ├── InventoryItem.java       → see Section 2.4
  └── StockMovement.java       → see Section 2.4
```
- **Pattern:** follow `Supplier.java` (user/team scoping, @PrePersist, @Builder.Default)
- FK relationships: InventoryItem → InventoryCategory, Supplier, User, Team
- StockMovement → InventoryItem, User, Supplier, Flock, CropPlan, FarmLocation, Transaction
- **Verify:** `./mvnw compile` — no errors

**Step 4: JPA Repositories** (3 files)
```
📁 ma/farmsense/repository/
  ├── InventoryCategoryRepository.java
  ├── InventoryItemRepository.java
  └── StockMovementRepository.java
```
- `InventoryItemRepository`: add `findByUserAndDeletedAtIsNull()`, `findByUserAndStatusIn()`, `findByUserAndCategoryType()`
- `StockMovementRepository`: add `findByItemIdOrderByCreatedAtDesc()`, `findByUserIdAndMovementDateBetween()`
- **Pattern:** copy `SupplierRepository.java`
- **Verify:** `./mvnw compile` — no errors

---

### 🟡 PHASE 2 — DTOs & Services (Days 2–3)

**Step 5: DTOs** (~10 files)
```
📁 ma/farmsense/dto/inventory/
  ├── CreateInventoryItemRequest.java    → name*, categoryId*, unit*, minStockLevel*, currentQuantity, lastUnitCost, supplierId, sku, storageLocation, expiryDate, notes
  ├── UpdateInventoryItemRequest.java    → all fields optional
  ├── InventoryItemResponse.java         → all item fields + categoryName + supplierName + status (computed)
  ├── CreateStockMovementRequest.java    → itemId*, type*, quantity*, unitCost, supplierId, referenceNumber, reason, flockId, cropPlanId, farmLocationId, movementDate, notes
  ├── StockMovementResponse.java         → all movement fields + itemName + supplierName + flockName etc.
  ├── CreateCategoryRequest.java         → name*, type*, description, color, icon
  ├── UpdateCategoryRequest.java         → all optional
  ├── CategoryResponse.java             → id, name, type, description, color, icon
  ├── InventoryDashboardResponse.java   → totalItems, lowStockCount, totalValue, stockByCategory[], recentMovements[], lowStockItems[]
  └── InventoryFilterRequest.java       → search, categoryId, status, supplierId (all optional query params)
```
- **Pattern:** copy `dto/accounting/CreateTransactionRequest.java` style (jakarta.validation annotations)
- **Verify:** `./mvnw compile`

**Step 6: `InventoryCategoryService`** — SIMPLEST, do this first
```
📁 ma/farmsense/service/InventoryCategoryService.java
```
- `findAll(User user)` — return all categories for user, seed defaults if empty
- `create(User user, CreateCategoryRequest req)` — create category
- `update(User user, UUID id, UpdateCategoryRequest req)` — update
- `delete(User user, UUID id)` — delete
- **Seed logic:** if `findAll` returns empty, auto-create 8 default categories (FEED, SEEDS, etc.) with French names
- **Pattern:** copy `SupplierService.java` exactly
- **Verify:** `./mvnw compile`

**Step 7: `InventoryItemService`** — CORE, most important
```
📁 ma/farmsense/service/InventoryItemService.java
```
- `findAll(User user, InventoryFilterRequest filter)` — list with search + category + status filter
- `findById(User user, UUID id)` — get single item (throw 404 if not found or deleted)
- `create(User user, CreateInventoryItemRequest req)` — create item, compute initial status
- `update(User user, UUID id, UpdateInventoryItemRequest req)` — update item
- `delete(User user, UUID id)` — soft delete (set deletedAt)
- `findLowStock(User user)` — items where currentQuantity ≤ minStockLevel
- `getDashboard(User user)` — aggregate KPIs: totalItems, lowStockCount, totalValue, stockByCategory
- **Status computation:** ACTIVE if qty > minStockLevel, LOW_STOCK if qty ≤ minStockLevel && qty > 0, OUT_OF_STOCK if qty ≤ 0
- **Pattern:** copy `TransactionService.java` for filter/query pattern
- **Verify:** `./mvnw compile`

**Step 8: `StockMovementService`** — CRITICAL BUSINESS LOGIC
```
📁 ma/farmsense/service/StockMovementService.java
```
- `record(User user, CreateStockMovementRequest req)`:
  1. Load InventoryItem (throw 404 if not found)
  2. Validate: USAGE quantity cannot exceed currentQuantity
  3. Save quantityBefore = item.currentQuantity
  4. Update item.currentQuantity: PURCHASE → add, USAGE → subtract, ADJUSTMENT → add/subtract
  5. Update item.lastUnitCost if PURCHASE
  6. Recompute item.status (ACTIVE / LOW_STOCK / OUT_OF_STOCK)
  7. Save quantityAfter = item.currentQuantity
  8. Save StockMovement
  9. Return MovementResponse
- `findByItem(User user, UUID itemId)` — movement history for one item
- `findAll(User user, type filter, date range)` — all movements
- **⚠️ IMPORTANT:** This is the only place that changes `currentQuantity`. Never update it directly.
- **Verify:** `./mvnw compile`

---

### 🟢 PHASE 3 — Controllers (Day 4)

**Step 9: `InventoryCategoryController`**
```
📁 ma/farmsense/controller/InventoryCategoryController.java
@RequestMapping("/api/v1/inventory/categories")
```
| Method | Path | Action |
|--------|------|--------|
| GET | `/` | List all categories |
| POST | `/` | Create category |
| PUT | `/{id}` | Update category |
| DELETE | `/{id}` | Delete category |

- **Pattern:** copy `SupplierController.java` exactly — same structure
- **Verify:** `./mvnw spring-boot:run` + `curl localhost:8080/api/v1/inventory/categories`

**Step 10: `InventoryItemController`**
```
📁 ma/farmsense/controller/InventoryItemController.java
@RequestMapping("/api/v1/inventory/items")
```
| Method | Path | Action |
|--------|------|--------|
| GET | `/` | List items (with filter query params) |
| POST | `/` | Create item |
| GET | `/{id}` | Get item detail |
| PUT | `/{id}` | Update item |
| DELETE | `/{id}` | Soft-delete item |
| GET | `/dashboard` | Dashboard KPIs |
| GET | `/low-stock` | Low-stock items only |

- **⚠️ ROUTE ORDER:** Put `/dashboard` and `/low-stock` BEFORE `/{id}` to avoid UUID parse errors
- **Pattern:** copy `TransactionController.java`
- **Verify:** `curl localhost:8080/api/v1/inventory/items` returns `[]`

**Step 11: `StockMovementController`**
```
📁 ma/farmsense/controller/StockMovementController.java
@RequestMapping("/api/v1/inventory/movements")
```
| Method | Path | Action |
|--------|------|--------|
| GET | `/` | List all movements |
| POST | `/` | Record movement |
| GET | `/{id}` | Get movement detail |
| GET | `/by-item/{itemId}` | Movements for one item |

- **Verify:** Full smoke test with Postman/curl:
  1. POST category → POST item → POST movement (PURCHASE) → GET item (check qty updated)

---

### 🔵 PHASE 4 — Alerts & Integration (Day 4–5)

**Step 12: Low-Stock Alert Trigger**
- In `StockMovementService.record()`, after updating item status:
  - If new status is `LOW_STOCK` or `OUT_OF_STOCK`, create an Alert:
    ```java
    Alert alert = Alert.builder()
      .user(user)
      .type("LOW_STOCK")
      .severity(status == OUT_OF_STOCK ? "HIGH" : "MEDIUM")
      .msgFr("Stock bas: " + item.getName() + " — " + item.getCurrentQuantity() + " " + item.getUnit())
      .msgEn("Low stock: " + item.getName() + " — " + item.getCurrentQuantity() + " " + item.getUnit())
      .msgAr("مخزون منخفض: " + item.getName())
      .build();
    ```
  - Check: don't create duplicate alerts (query existing unacked LOW_STOCK alert for same item)
- **Verify:** POST a USAGE movement that drops below minStockLevel → check `/api/v1/alerts` has new alert

**Step 13: Auto-Expense on USAGE** (optional, can defer)
- In `StockMovementService.record()`, if type == USAGE and item.lastUnitCost > 0:
  - Create an EXPENSE transaction via `TransactionService.create()`:
    ```java
    CreateTransactionRequest txReq = new CreateTransactionRequest();
    txReq.setType(TransactionType.EXPENSE);
    txReq.setCategory("INVENTORY");
    txReq.setAmount(qty * lastUnitCost);
    txReq.setDescription("Usage: " + item.getName());
    txReq.setTransactionDate(movementDate);
    ```
  - Save the transaction ID on the StockMovement record
- **Verify:** POST USAGE movement → check `/api/v1/transactions` has new EXPENSE

**Step 14: Backend Unit Tests**
```
📁 apps/backend/src/test/java/ma/farmsense/service/
  ├── InventoryCategoryServiceTest.java
  ├── InventoryItemServiceTest.java
  └── StockMovementServiceTest.java
```
- Test: create item → record PURCHASE → verify qty increased
- Test: record USAGE exceeding stock → expect validation error
- Test: record USAGE that triggers LOW_STOCK → verify alert created
- Test: soft delete item → verify not returned in findAll
- **Pattern:** copy `PlantServiceTest.java`
- **Verify:** `./mvnw test -pl apps/backend`

---

### 🟣 PHASE 5 — Remaining Frontend Views (Days 6–9)

> The dashboard view, store, types, routes, nav, and i18n are already done (✅).
> These are the remaining frontend screens.

**Step 15: `InventoryItemFormView.vue`** — Add/Edit item
```
📁 apps/frontend/src/views/inventory/InventoryItemFormView.vue
```
- Route: `/inventory/items/new` and `/inventory/items/:id/edit`
- Fields: name, category (dropdown), unit (dropdown), initialQty, minStock, unitCost, supplier (dropdown), storageLocation, expiryDate, notes
- Use form patterns from `TransactionFormView.vue`
- Add routes to `router/index.ts`

**Step 16: `InventoryItemDetailView.vue`** — Item detail + movement history
```
📁 apps/frontend/src/views/inventory/InventoryItemDetailView.vue
```
- Route: `/inventory/items/:id`
- Show item info + KPI cards (current qty, min stock, value, last cost)
- Show full movement history table (date, type, qty, before/after, linked activity, user)
- "Record Movement" button opens the existing StockMovementDialog

**Step 17: `StockMovementsView.vue`** — Movement log page
```
📁 apps/frontend/src/views/inventory/StockMovementsView.vue
```
- Route: `/inventory/movements` (replace ComingSoonView)
- Full table: date, item name, type badge, qty (+/-), cost, linked activity, user
- Filter by: type (PURCHASE/USAGE/ADJUSTMENT), date range, item

**Step 18: Connect Dashboard to Real API**
- Replace demo data in `InventoryDashboardView.vue` with real store data
- Call `inventoryStore.fetchItems()` on mount
- KPI cards compute from store: feedKpi from FEED items, seedsKpi from SEEDS items, etc.

---

### ⬛ PHASE 6 — Integration & Polish (Day 10)

**Step 19:** End-to-end flow test:
1. Login → Navigate to `/inventory`
2. Click "+ Log Usage" → Add a PURCHASE for new feed
3. See dashboard update with new stock levels
4. Click item → See detail page with movement history
5. Record a USAGE linked to a flock
6. See low-stock alert appear in sidebar
7. Switch to Arabic → Verify RTL layout
8. Test on mobile (375px)

**Step 20:** Bug fixes + polish — Fix any issues found in step 19

---

### 📊 Story Dependency Graph

```
US-1 (Item CRUD)          ← FOUNDATION — start here
  │
  ├── US-4 (List + Filters) ← needs items to exist
  │
  ├── US-2 (Stock Movements) ← needs items to record against
  │     │
  │     ├── US-5 (Activity Linking) ← extends movement recording
  │     │
  │     └── US-6 (Low-Stock Alerts) ← triggered by movements
  │
  └── US-3 (Dashboard) ← needs items + movements for KPIs

US-7 (i18n) ← DONE ✅ — independent, already completed
```

### 🎯 Recommended Execution Order

```
┌─────┬────────────┬──────────────────────────────────────┬─────────────┐
│  #  │ User Story │ What to Build                        │ Depends On  │
├─────┼────────────┼──────────────────────────────────────┼─────────────┤
│  1  │ US-1       │ Migration + Entities + Item CRUD     │ Nothing     │
│     │            │ (Steps 1–7, 9–10)                    │             │
├─────┼────────────┼──────────────────────────────────────┼─────────────┤
│  2  │ US-2       │ StockMovementService + Controller    │ US-1        │
│     │            │ (Steps 8, 11)                        │             │
├─────┼────────────┼──────────────────────────────────────┼─────────────┤
│  3  │ US-4       │ Item List View + Filters (FE)        │ US-1        │
│     │            │ (Step 15, 16)                        │             │
├─────┼────────────┼──────────────────────────────────────┼─────────────┤
│  4  │ US-6       │ Low-Stock Alert Trigger              │ US-2        │
│     │            │ (Step 12)                            │             │
├─────┼────────────┼──────────────────────────────────────┼─────────────┤
│  5  │ US-3       │ Dashboard → connect to real API      │ US-1, US-2  │
│     │            │ (Step 17, 18)                        │             │
├─────┼────────────┼──────────────────────────────────────┼─────────────┤
│  6  │ US-5       │ Activity Linking + Auto-Expense      │ US-2        │
│     │            │ (Step 13)                            │             │
├─────┼────────────┼──────────────────────────────────────┼─────────────┤
│  7  │ US-7       │ ✅ DONE — i18n complete              │ None        │
├─────┼────────────┼──────────────────────────────────────┼─────────────┤
│  8  │ —          │ Integration test + polish            │ All         │
│     │            │ (Steps 19–20)                        │             │
└─────┴────────────┴──────────────────────────────────────┴─────────────┘
```

### ⏱️ Day-by-Day Implementation Schedule

```
DAY 1  │ US-1 backend: Migration → Enums → Entities → Repos → DTOs
       │ ✓ Verify: ./mvnw compile passes, tables created in DB
       │
DAY 2  │ US-1 backend: CategoryService → ItemService → Controllers
       │ ✓ Verify: curl POST/GET items works, categories auto-seed
       │
DAY 3  │ US-2 backend: StockMovementService → Controller
       │ ✓ Verify: POST movement updates item qty, curl full flow works
       │
DAY 4  │ US-6 backend: Low-stock alert trigger in MovementService
       │ US-5 backend: Auto-expense creation on USAGE (optional)
       │ ✓ Verify: USAGE below threshold → alert created
       │
DAY 5  │ US-1,2 backend: Unit tests (3 test classes)
       │ ✓ Verify: ./mvnw test passes
       │
DAY 6  │ US-3 frontend: Connect dashboard to real API (replace demo data)
       │ ✓ Verify: Dashboard shows real items from DB
       │
DAY 7  │ US-1 frontend: InventoryItemFormView (add/edit item)
       │ ✓ Verify: Can create item via UI, appears in dashboard
       │
DAY 8  │ US-4 frontend: InventoryItemDetailView + movement history
       │ ✓ Verify: Click item → see detail + movements
       │
DAY 9  │ US-2 frontend: StockMovementsView (movement log page)
       │ ✓ Verify: /inventory/movements shows full log, filters work
       │
DAY 10 │ Integration test + mobile pass + bug fixes
       │ ✓ Verify: Full end-to-end flow in FR/AR/EN, desktop + mobile
```

---

## Reference Files (Patterns to Follow)

| Pattern | Reference File |
|---------|---------------|
| Entity | `entity/Supplier.java` (user/team scoping, @PrePersist, Lombok) |
| Service | `service/SupplierService.java` (simple CRUD), `service/TransactionService.java` (filters) |
| Controller | `controller/SupplierController.java` (@AuthenticationPrincipal User, ResponseEntity) |
| DTOs | `dto/accounting/CreateTransactionRequest.java` (jakarta.validation) |
| Migration | `db/migration/V12__accounting_module.sql` |
| Pinia Store | `stores/inventory.store.ts` ✅ (already created) |
| Dashboard View | `views/inventory/InventoryDashboardView.vue` ✅ (already created) |
| List View | `views/accounting/TransactionsView.vue` |
| Form View | `views/accounting/TransactionFormView.vue` |
| Nav Config | `config/navigation.ts` ✅ (already updated) |
