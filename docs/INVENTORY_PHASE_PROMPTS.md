# Inventory Module — Phase-by-Phase Dev Prompts

> Copy-paste each prompt when you're ready to start that phase.
> Each prompt is self-contained with all context needed.

---

## PHASE 1 — Database & Entities

```
You are working on a Spring Boot 3 + Java 17 monorepo at apps/backend.
Package: ma.farmsense. Database: PostgreSQL 15. ORM: JPA/Hibernate. Migrations: Flyway.

Create the Inventory Management module database layer. Follow existing patterns exactly.

### Step 1: Flyway Migration
Create file: src/main/resources/db/migration/V13__inventory_module.sql

Create 3 tables:

1. inventory_categories
   - id UUID PK DEFAULT gen_random_uuid()
   - user_id UUID NOT NULL FK → users(id) ON DELETE CASCADE
   - name VARCHAR(100) NOT NULL
   - type VARCHAR(20) NOT NULL DEFAULT 'OTHER'
   - description TEXT
   - color VARCHAR(7) DEFAULT '#6B7280'
   - icon VARCHAR(50)
   - created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
   - UNIQUE(user_id, name)
   - Index: idx_inventory_categories_user ON (user_id)

2. inventory_items
   - id UUID PK DEFAULT gen_random_uuid()
   - user_id UUID NOT NULL FK → users(id) ON DELETE CASCADE
   - team_id UUID FK → teams(id) ON DELETE SET NULL
   - category_id UUID FK → inventory_categories(id) ON DELETE SET NULL
   - supplier_id UUID FK → suppliers(id) ON DELETE SET NULL
   - name VARCHAR(200) NOT NULL
   - sku VARCHAR(50)
   - barcode VARCHAR(100)
   - unit VARCHAR(20) NOT NULL DEFAULT 'UNIT'
   - current_quantity DECIMAL(12,2) NOT NULL DEFAULT 0
   - min_stock_level DECIMAL(12,2) NOT NULL DEFAULT 0
   - max_stock_level DECIMAL(12,2)
   - reorder_point DECIMAL(12,2) NOT NULL DEFAULT 0
   - last_unit_cost DECIMAL(12,2) DEFAULT 0
   - storage_location VARCHAR(200)
   - expiry_date DATE
   - batch_number VARCHAR(100)
   - notes TEXT
   - image_url TEXT
   - status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
   - created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
   - updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
   - deleted_at TIMESTAMPTZ
   - UNIQUE(user_id, name)
   - Indexes: user_id, team_id, category_id, supplier_id, status

3. stock_movements
   - id UUID PK DEFAULT gen_random_uuid()
   - item_id UUID NOT NULL FK → inventory_items(id) ON DELETE CASCADE
   - user_id UUID NOT NULL FK → users(id) ON DELETE CASCADE
   - team_id UUID FK → teams(id) ON DELETE SET NULL
   - type VARCHAR(20) NOT NULL
   - quantity DECIMAL(12,2) NOT NULL
   - unit_cost DECIMAL(12,2)
   - total_cost DECIMAL(12,2)
   - supplier_id UUID FK → suppliers(id) ON DELETE SET NULL
   - reference_number VARCHAR(100)
   - reason VARCHAR(50)
   - reference_type VARCHAR(20)
   - reference_id UUID
   - flock_id UUID FK → flocks(id) ON DELETE SET NULL
   - crop_plan_id UUID FK → crop_plans(id) ON DELETE SET NULL
   - farm_location_id UUID FK → farm_locations(id) ON DELETE SET NULL
   - transaction_id UUID FK → transactions(id) ON DELETE SET NULL
   - movement_date DATE NOT NULL DEFAULT CURRENT_DATE
   - notes TEXT
   - quantity_before DECIMAL(12,2) NOT NULL
   - quantity_after DECIMAL(12,2) NOT NULL
   - created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
   - Indexes: item_id, user_id, type, movement_date, flock_id, crop_plan_id

### Step 2: Java Enums (in ma.farmsense.entity package)
Create 5 enum files following the pattern of CropCategory.java:

- InventoryCategoryType: FEED, SEEDS, FERTILIZER, PESTICIDE, VETERINARY, EQUIPMENT, PACKAGING, OTHER
- StockMovementType: PURCHASE, USAGE, ADJUSTMENT, TRANSFER, LOSS
- InventoryUnit: KG, G, L, ML, UNIT, BAG, BOX, BOTTLE, TON, DOSE
- InventoryStatus: ACTIVE, LOW_STOCK, OUT_OF_STOCK, ARCHIVED
- AdjustmentReason: DAMAGE, EXPIRY, COUNT_CORRECTION, LOSS, OTHER

Also add LOW_STOCK to the existing Alert.AlertType enum (add it after DEVICE_OFFLINE).

### Step 3: JPA Entities (in ma.farmsense.entity package)
Create 3 entity files following the exact pattern of Supplier.java and Transaction.java:
- Use @Entity, @Table, @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
- Use @ManyToOne(fetch = FetchType.LAZY) for all FK relationships
- Use @Builder.Default for default values
- Use @PreUpdate for updatedAt
- InventoryItem.createdAt uses @Column(nullable = false, updatable = false)

1. InventoryCategory — fields matching the migration
2. InventoryItem — fields matching the migration, with FKs to: User, Team, InventoryCategory, Supplier
3. StockMovement — fields matching the migration, with FKs to: InventoryItem, User, Team, Supplier, Flock, CropPlan, FarmLocation, Transaction

### Step 4: JPA Repositories (in ma.farmsense.repository package)
Following the pattern of SupplierRepository.java:

1. InventoryCategoryRepository extends JpaRepository<InventoryCategory, UUID>
   - List<InventoryCategory> findByUserOrderByNameAsc(User user)

2. InventoryItemRepository extends JpaRepository<InventoryItem, UUID>
   - List<InventoryItem> findByUserAndDeletedAtIsNullOrderByNameAsc(User user)
   - List<InventoryItem> findByUserAndDeletedAtIsNullAndStatusInOrderByNameAsc(User user, List<InventoryStatus> statuses)
   - List<InventoryItem> findByUserAndDeletedAtIsNullAndNameContainingIgnoreCaseOrderByNameAsc(User user, String name)
   - List<InventoryItem> findByUserAndDeletedAtIsNullAndCategory_TypeOrderByNameAsc(User user, InventoryCategoryType type)
   - long countByUserAndDeletedAtIsNull(User user)
   - long countByUserAndDeletedAtIsNullAndStatusIn(User user, List<InventoryStatus> statuses)

3. StockMovementRepository extends JpaRepository<StockMovement, UUID>
   - List<StockMovement> findByItem_IdOrderByCreatedAtDesc(UUID itemId)
   - List<StockMovement> findByUser_IdOrderByCreatedAtDesc(UUID userId)
   - List<StockMovement> findByUser_IdAndMovementDateBetweenOrderByCreatedAtDesc(UUID userId, LocalDate from, LocalDate to)

### Verification
After all files are created, run: ./mvnw compile
All classes must compile without errors. Do NOT run the app yet (DB may not be up).
```

---

## PHASE 2 — DTOs & Services

```
You are continuing the Inventory Management module for FarmSense (Spring Boot 3, Java 17).
Phase 1 (migration, enums, entities, repos) is already done.

Now create DTOs and Services. Follow existing patterns exactly.

### Step 5: DTOs (in ma.farmsense.dto.inventory package)
Follow the pattern of CreateSupplierRequest.java and SupplierResponse.java.
Use @Data @Builder @NoArgsConstructor @AllArgsConstructor on all DTOs.
Use jakarta.validation annotations (@NotBlank, @NotNull, @Size, @DecimalMin, etc.).

1. CreateCategoryRequest
   - @NotBlank @Size(max=100) String name
   - @NotNull InventoryCategoryType type
   - String description, String color, String icon

2. UpdateCategoryRequest
   - String name, InventoryCategoryType type, String description, String color, String icon

3. CategoryResponse
   - UUID id, String name, InventoryCategoryType type, String description, String color, String icon, Instant createdAt
   - static CategoryResponse from(InventoryCategory c) — mapper method

4. CreateInventoryItemRequest
   - @NotBlank @Size(max=200) String name
   - UUID categoryId (optional)
   - String sku, String barcode
   - @NotNull InventoryUnit unit
   - BigDecimal currentQuantity (default 0)
   - @NotNull @DecimalMin("0") BigDecimal minStockLevel
   - BigDecimal maxStockLevel
   - @NotNull @DecimalMin("0") BigDecimal reorderPoint
   - BigDecimal lastUnitCost
   - UUID supplierId
   - String storageLocation, LocalDate expiryDate, String batchNumber, String notes

5. UpdateInventoryItemRequest — all fields from Create but all optional (no @NotNull/@NotBlank)

6. InventoryItemResponse
   - UUID id, String name, UUID categoryId, String categoryName, String categoryType
   - String sku, String barcode, String unit
   - BigDecimal currentQuantity, BigDecimal minStockLevel, BigDecimal maxStockLevel, BigDecimal reorderPoint
   - BigDecimal lastUnitCost
   - UUID supplierId, String supplierName
   - String storageLocation, LocalDate expiryDate, String batchNumber, String notes, String imageUrl
   - String status, int stockPercentage
   - Instant createdAt, Instant updatedAt
   - static InventoryItemResponse from(InventoryItem item) — mapper:
     * categoryName = item.getCategory() != null ? item.getCategory().getName() : null
     * categoryType = item.getCategory() != null ? item.getCategory().getType().name() : null
     * supplierName = item.getSupplier() != null ? item.getSupplier().getName() : null
     * stockPercentage = compute: if maxStockLevel > 0 then (currentQuantity / maxStockLevel * 100).intValue(), else if minStockLevel > 0 then (currentQuantity / (minStockLevel * 2) * 100).intValue(), else 100

7. CreateStockMovementRequest
   - @NotNull UUID itemId
   - @NotNull StockMovementType type
   - @NotNull @DecimalMin("0.01") BigDecimal quantity
   - BigDecimal unitCost
   - UUID supplierId
   - String referenceNumber, String reason
   - String referenceType (FEEDING, PLANTING, TREATMENT, IRRIGATION, HARVEST, OTHER)
   - UUID flockId, UUID cropPlanId, UUID farmLocationId
   - LocalDate movementDate (default today)
   - String notes

8. StockMovementResponse
   - UUID id, UUID itemId, String itemName
   - String type, BigDecimal quantity
   - BigDecimal quantityBefore, BigDecimal quantityAfter
   - BigDecimal unitCost, BigDecimal totalCost
   - UUID supplierId, String supplierName, String referenceNumber, String reason
   - String referenceType
   - UUID flockId, String flockName, UUID cropPlanId, String cropPlanName
   - String performedBy, LocalDate movementDate, String notes, Instant createdAt
   - static StockMovementResponse from(StockMovement m) — mapper with null-safe FK name resolution

9. InventoryDashboardResponse
   - long totalItems, long lowStockCount, long criticalCount
   - BigDecimal totalValue
   - List<CategoryStock> stockByCategory (inner record: String category, String categoryType, long count, BigDecimal value)
   - List<StockMovementResponse> recentMovements
   - List<InventoryItemResponse> lowStockItems

### Step 6: InventoryCategoryService (in ma.farmsense.service)
Follow SupplierService.java exactly.

- findAll(User user): get all categories for user. If empty, seed 8 defaults:
  FEED("Alimentation animale"), SEEDS("Semences"), FERTILIZER("Engrais"), PESTICIDE("Pesticides"),
  VETERINARY("Médicaments vétérinaires"), EQUIPMENT("Équipement"), PACKAGING("Emballage"), OTHER("Autre")
- create(User user, CreateCategoryRequest req): build + save
- update(User user, UUID id, UpdateCategoryRequest req): getOwned + set fields + save
- delete(User user, UUID id): getOwned + delete
- getOwned(User user, UUID id): findById + ownership check via user.getId() match

### Step 7: InventoryItemService (in ma.farmsense.service)
Follow TransactionService.java for the filter pattern.

- findAll(User user, String search, InventoryCategoryType categoryType, InventoryStatus status):
  * Start with findByUserAndDeletedAtIsNull
  * Filter by search (name contains), categoryType, status as needed
  * Return list of InventoryItemResponse
- findById(User user, UUID id): getOwned + return InventoryItemResponse
- create(User user, CreateInventoryItemRequest req):
  * Resolve category (if categoryId provided, load it)
  * Resolve supplier (if supplierId provided, load it)
  * Build InventoryItem with all fields
  * Compute status: computeStatus(currentQuantity, minStockLevel)
  * Save and return response
- update(User user, UUID id, UpdateInventoryItemRequest req):
  * getOwned, set non-null fields, recompute status, save
- delete(User user, UUID id): getOwned, set deletedAt = Instant.now(), save
- findLowStock(User user):
  * findByUserAndDeletedAtIsNullAndStatusIn(user, List.of(LOW_STOCK, OUT_OF_STOCK))
- getDashboard(User user): aggregate counts, total value (sum qty × lastUnitCost), group by category
- computeStatus(BigDecimal qty, BigDecimal minStock):
  * qty <= 0 → OUT_OF_STOCK
  * qty <= minStock → LOW_STOCK
  * else → ACTIVE

### Step 8: StockMovementService (in ma.farmsense.service)
THIS IS THE MOST CRITICAL SERVICE. All stock changes go through here.

- record(User user, CreateStockMovementRequest req):
  1. Load item via InventoryItemService.getOwned(user, req.getItemId())
  2. Validate: if type==USAGE and quantity > currentQuantity → throw AppException.badRequest("Insufficient stock")
  3. quantityBefore = item.getCurrentQuantity()
  4. Compute new quantity:
     - PURCHASE → add quantity
     - USAGE → subtract quantity
     - ADJUSTMENT → add quantity (can be negative)
     - LOSS → subtract quantity
     - TRANSFER → subtract quantity
  5. totalCost = quantity × (unitCost or item.lastUnitCost)
  6. If PURCHASE and unitCost != null: item.setLastUnitCost(unitCost)
  7. item.setCurrentQuantity(newQuantity)
  8. item.setStatus(computeStatus(newQuantity, item.getMinStockLevel()))
  9. Save item
  10. Build StockMovement with quantityBefore, quantityAfter=newQuantity, resolve FKs (supplier, flock, cropPlan, farmLocation)
  11. Save movement
  12. Return StockMovementResponse

- findByItem(UUID itemId): repo query, map to response
- findAll(User user): repo query, map to response

### Verification
Run: ./mvnw compile — must pass with zero errors.
```

---

## PHASE 3 — Controllers

```
You are continuing the Inventory Management module for FarmSense (Spring Boot 3, Java 17).
Phase 1 (migration, entities) and Phase 2 (DTOs, services) are done.

Now create REST controllers. Follow SupplierController.java pattern exactly:
- @RestController, @RequestMapping, @RequiredArgsConstructor
- @AuthenticationPrincipal User user on every method
- Return ResponseEntity<T>
- Use @Valid on request bodies
- POST returns status 201

### Step 9: InventoryCategoryController
@RequestMapping("/api/v1/inventory/categories")

| Method | Path | Params | Return |
|--------|------|--------|--------|
| GET    | /    | —      | List<CategoryResponse> |
| POST   | /    | @Valid @RequestBody CreateCategoryRequest | CategoryResponse (201) |
| PUT    | /{id}| @PathVariable UUID id, @Valid @RequestBody UpdateCategoryRequest | CategoryResponse |
| DELETE | /{id}| @PathVariable UUID id | Void (204) |

### Step 10: InventoryItemController
@RequestMapping("/api/v1/inventory/items")

⚠️ CRITICAL: Put /dashboard and /low-stock BEFORE /{id} to avoid UUID parse errors.

| Method | Path         | Params | Return |
|--------|-------------|--------|--------|
| GET    | /           | @RequestParam(required=false) String search, @RequestParam(required=false) InventoryCategoryType categoryType, @RequestParam(required=false) InventoryStatus status | List<InventoryItemResponse> |
| POST   | /           | @Valid @RequestBody CreateInventoryItemRequest | InventoryItemResponse (201) |
| GET    | /dashboard  | — | InventoryDashboardResponse |
| GET    | /low-stock  | — | List<InventoryItemResponse> |
| GET    | /{id}       | @PathVariable UUID id | InventoryItemResponse |
| PUT    | /{id}       | @PathVariable UUID id, @Valid @RequestBody UpdateInventoryItemRequest | InventoryItemResponse |
| DELETE | /{id}       | @PathVariable UUID id | Void (204) |

### Step 11: StockMovementController
@RequestMapping("/api/v1/inventory/movements")

| Method | Path              | Params | Return |
|--------|------------------|--------|--------|
| GET    | /                | — | List<StockMovementResponse> |
| POST   | /                | @Valid @RequestBody CreateStockMovementRequest | StockMovementResponse (201) |
| GET    | /{id}            | @PathVariable UUID id | StockMovementResponse |
| GET    | /by-item/{itemId}| @PathVariable UUID itemId | List<StockMovementResponse> |

### Verification
1. Run: ./mvnw compile — must pass
2. Start app: ./mvnw spring-boot:run
3. Smoke test with curl (need valid JWT token from login):
   - GET /api/v1/inventory/categories → should return seeded defaults
   - POST /api/v1/inventory/items (with name, unit, minStockLevel) → 201
   - GET /api/v1/inventory/items → should include new item
   - POST /api/v1/inventory/movements (PURCHASE, quantity=100) → 201
   - GET /api/v1/inventory/items/{id} → currentQuantity should be 100
   - GET /api/v1/inventory/items/dashboard → should show aggregates
```

---

## PHASE 4 — Alerts & Auto-Expense

```
You are continuing the Inventory Management module for FarmSense (Spring Boot 3, Java 17).
Phases 1–3 (migration, entities, services, controllers) are done and working.

Now add business logic integrations.

### Step 12: Low-Stock Alert Trigger

Modify Alert.AlertType enum — add LOW_STOCK:
  File: src/main/java/ma/farmsense/entity/Alert.java
  Change: SOIL_DRY, SOIL_WET, TEMP_HIGH, TEMP_LOW, LIGHT_LOW, DEVICE_OFFLINE
  To:     SOIL_DRY, SOIL_WET, TEMP_HIGH, TEMP_LOW, LIGHT_LOW, DEVICE_OFFLINE, LOW_STOCK

Modify StockMovementService.record() — after saving the item with new status:

  if (item.getStatus() == InventoryStatus.LOW_STOCK || item.getStatus() == InventoryStatus.OUT_OF_STOCK) {
      // Check no existing unacked LOW_STOCK alert for this item
      boolean existingAlert = alertRepository.findByUserAndTypeAndAckAtIsNull(user, Alert.AlertType.LOW_STOCK)
          .stream()
          .anyMatch(a -> a.getMsgEn() != null && a.getMsgEn().contains(item.getName()));

      if (!existingAlert) {
          Alert alert = Alert.builder()
              .user(user)
              .type(Alert.AlertType.LOW_STOCK)
              .severity(item.getStatus() == InventoryStatus.OUT_OF_STOCK ? Alert.Severity.HIGH : Alert.Severity.MEDIUM)
              .msgFr("Stock bas: " + item.getName() + " — " + item.getCurrentQuantity() + " " + item.getUnit().name().toLowerCase())
              .msgEn("Low stock: " + item.getName() + " — " + item.getCurrentQuantity() + " " + item.getUnit().name().toLowerCase())
              .msgAr("مخزون منخفض: " + item.getName() + " — " + item.getCurrentQuantity() + " " + item.getUnit().name().toLowerCase())
              .build();
          alertRepository.save(alert);
      }
  }

Add AlertRepository dependency to StockMovementService.
You may need to add this query method to AlertRepository if it doesn't exist:
  List<Alert> findByUserAndTypeAndAckAtIsNull(User user, Alert.AlertType type);

### Step 13: Auto-Expense on USAGE (Optional but recommended)

In StockMovementService.record(), after saving movement, if type == USAGE:

  if (req.getType() == StockMovementType.USAGE && item.getLastUnitCost() != null && item.getLastUnitCost().compareTo(BigDecimal.ZERO) > 0) {
      BigDecimal expenseAmount = req.getQuantity().multiply(item.getLastUnitCost());

      CreateTransactionRequest txReq = new CreateTransactionRequest();
      txReq.setType(TransactionType.EXPENSE);
      txReq.setCategory("INVENTORY");
      txReq.setSubcategory(item.getCategory() != null ? item.getCategory().getName() : null);
      txReq.setAmount(expenseAmount);
      txReq.setQuantity(req.getQuantity().doubleValue());
      txReq.setUnitPrice(item.getLastUnitCost());
      txReq.setTransactionDate(movement.getMovementDate());
      txReq.setDescription("Inventory usage: " + item.getName());

      if (req.getFlockId() != null) txReq.setFlockId(req.getFlockId());
      if (req.getCropPlanId() != null) txReq.setCropPlanId(req.getCropPlanId());
      if (req.getFarmLocationId() != null) txReq.setFarmLocationId(req.getFarmLocationId());

      TransactionResponse tx = transactionService.create(user, txReq);
      // Link transaction to movement
      movement.setTransaction(transactionRepository.findById(tx.getId()).orElse(null));
      stockMovementRepository.save(movement);
  }

Add TransactionService and TransactionRepository dependencies to StockMovementService.

### Step 14: Backend Unit Tests

Create 3 test files in src/test/java/ma/farmsense/service/
Follow the pattern of PlantServiceTest.java (use @SpringBootTest or mock with @ExtendWith(MockitoExtension.class)).

InventoryItemServiceTest.java:
- test_createItem_setsStatusActive()
- test_createItem_withLowQuantity_setsStatusLowStock()
- test_deleteItem_softDeletes()
- test_findAll_excludesDeletedItems()
- test_findLowStock_returnsOnlyLowAndOutOfStock()

StockMovementServiceTest.java:
- test_recordPurchase_increasesQuantity()
- test_recordUsage_decreasesQuantity()
- test_recordUsage_exceedingStock_throwsBadRequest()
- test_recordUsage_belowMinStock_createsAlert()
- test_recordPurchase_updatesLastUnitCost()

InventoryCategoryServiceTest.java:
- test_findAll_seedsDefaultsWhenEmpty()
- test_create_savesCategory()
- test_delete_removesCategory()

### Verification
Run: ./mvnw test — all tests must pass.
Then manually test the alert trigger:
1. Create an item with minStockLevel=50, currentQuantity=60
2. POST a USAGE movement with quantity=15 (new qty=45, below min)
3. GET /api/v1/alerts → should have a LOW_STOCK alert for the item
```

---

## PHASE 5 — Remaining Frontend Views

```
You are working on the FarmSense Vue 3 frontend (TypeScript, Vite, Tailwind CSS, Pinia).
The inventory module already has:
- ✅ types/index.ts — all inventory types defined
- ✅ stores/inventory.store.ts — Pinia store with CRUD
- ✅ views/inventory/InventoryDashboardView.vue — dashboard with KPIs, items table, alerts, movement modal
- ✅ router/index.ts — /inventory, /inventory/items, /inventory/movements routes
- ✅ config/navigation.ts — Inventory nav group
- ✅ i18n/index.ts — ~55 inventory keys in FR/AR/EN

Now build the 3 remaining views. Follow existing patterns from views/accounting/.

### Step 15: InventoryItemFormView.vue
Create: src/views/inventory/InventoryItemFormView.vue
Route: Add to router/index.ts:
  { path: 'inventory/items/new', name: 'inventory-item-new', component: () => import('@/views/inventory/InventoryItemFormView.vue') }
  { path: 'inventory/items/:id/edit', name: 'inventory-item-edit', component: () => import('@/views/inventory/InventoryItemFormView.vue') }

Follow the pattern of TransactionFormView.vue:
- If route has :id param → edit mode (fetch item, populate form)
- Else → create mode
- Form sections:
  1. Basic Info: name*, category (select from inventoryStore.categories), unit (select from InventoryUnit enum), sku, barcode
  2. Stock Settings: initialQuantity, minStockLevel*, maxStockLevel, reorderPoint*, unitCost
  3. Details: supplier (select from suppliers), storageLocation, expiryDate (date picker), batchNumber, notes
- Submit calls inventoryStore.createItem() or inventoryStore.updateItem()
- After success, router.push('/inventory/items')
- Use Tailwind classes matching DashboardView.vue style: rounded-2xl, border-gray-100, shadow-sm
- Add i18n keys for form labels in all 3 languages

### Step 16: InventoryItemDetailView.vue
Create: src/views/inventory/InventoryItemDetailView.vue
Route: Add to router/index.ts:
  { path: 'inventory/items/:id', name: 'inventory-item-detail', component: () => import('@/views/inventory/InventoryItemDetailView.vue') }

⚠️ Put this route AFTER /inventory/items/new in the router to avoid conflicts.

Layout:
- Back button (← router.back()) + item name as title + [Edit] [Record Movement] buttons
- Info bar: category badge, unit, supplier, SKU, location, expiry, status badge
- 4 mini KPI cards: Current Stock, Min. Stock, Last Unit Cost, Total Value
- Movement History table:
  * Columns: Date, Type (badge), Quantity (+/-), Before, After, Linked To, Notes
  * Fetch via inventoryStore.fetchMovements(itemId)
  * Desktop table + mobile cards (same pattern as DashboardView)
- "Record Movement" button opens the same StockMovementDialog (reuse from dashboard or extract as component)

### Step 17: StockMovementsView.vue
Create: src/views/inventory/StockMovementsView.vue
Route: Update router/index.ts — change inventory/movements from ComingSoonView to:
  component: () => import('@/views/inventory/StockMovementsView.vue')

Layout — follow TransactionsView.vue pattern:
- Title: "Stock Movements" + "+ Record Movement" button
- Filter bar: type dropdown (All/Purchase/Usage/Adjustment/Loss), date range, search by item name
- Table columns: Date, Item Name, Type (color-coded badge), Quantity (+/-), Unit Cost, Total, Linked Activity, User
  * PURCHASE = green badge, positive qty
  * USAGE = amber badge, negative qty
  * ADJUSTMENT = blue badge
  * LOSS = red badge, negative qty
- Pagination
- Desktop table + mobile cards

### Step 18: Connect Dashboard to Real API
Modify: src/views/inventory/InventoryDashboardView.vue

Replace the static demoItems array with real data:
- Remove the demoItems const
- Change allItems computed to just return inventoryStore.items
- Update KPI cards to compute from real data:
  * feedKpi: sum currentStock of items where categoryType === 'FEED'
  * fertilizerKpi: sum currentStock of items where categoryType === 'FERTILIZER'
  * seedsKpi: sum currentStock of items where categoryType === 'SEEDS'
  * medicationKpi: sum currentStock of items where categoryType === 'VETERINARY'
  * Trend labels: compute from dashboard response if available, else show 'N/A'
- Keep demo data as fallback: if inventoryStore.items.length === 0, show demo data (for when backend is not running)
- Add click handlers on table rows: router.push(`/inventory/items/${item.id}`)
- Add "+ Add Item" secondary button next to "Log Usage" that navigates to /inventory/items/new

### Add i18n keys for new views
Add to i18n/index.ts in all 3 languages (FR/AR/EN):
- inventory_add_item, inventory_edit_item, inventory_item_detail
- inventory_basic_info, inventory_stock_settings, inventory_details
- inventory_sku, inventory_barcode, inventory_storage_location, inventory_expiry_date, inventory_batch_number
- inventory_initial_quantity, inventory_min_stock, inventory_max_stock, inventory_reorder_point, inventory_unit_cost
- inventory_save, inventory_back, inventory_current_stock, inventory_total_value, inventory_last_cost
- inventory_movement_history, inventory_movement_date, inventory_linked_to, inventory_performed_by
- inventory_all_movements, inventory_filter_type, inventory_filter_date

### Verification
Run: cd apps/frontend && npx vite build — must pass with 0 errors.
Test in browser: navigate to each route, verify forms work, verify tables render.
```

---

## PHASE 6 — Integration & Polish

```
You are doing final integration testing and polish for the FarmSense Inventory module.
All backend (Phase 1–4) and frontend (Phase 5) code is complete.

### Step 19: End-to-End Integration Test

Run the full stack:
  cd apps/backend && docker-compose up -d && ./mvnw spring-boot:run &
  cd apps/frontend && npm run dev

Login with test credentials and perform this exact test flow:

1. Navigate to /inventory → Dashboard loads with demo data (or real data if items exist)
2. Click "+ Add Item" → Form opens at /inventory/items/new
3. Fill form: name="Corn Seeds", category=Seeds, unit=BAG, minStock=10, reorderPoint=20, currentQty=50
4. Submit → Redirects to /inventory/items, new item appears in list
5. Click the item row → Detail page at /inventory/items/:id
6. Click "Record Movement" → Modal opens
7. Record a PURCHASE: qty=100, unitCost=85 → Stock goes from 50 to 150
8. See movement in history table
9. Go back to dashboard → KPI for Seeds should update
10. Record a USAGE: qty=145 → Stock drops to 5 (below minStock=10)
11. Check /alerts → LOW_STOCK alert should appear
12. Navigate to /inventory/movements → See all movements in log
13. Filter by type=USAGE → Only usage movements shown

### Step 20: Cross-Browser & Responsive Testing

1. Desktop (1440px): Verify table columns all visible, sidebar correct
2. Tablet (768px): Verify layout adapts, table scrollable
3. Mobile (375px): Verify cards instead of tables, forms usable

### Step 21: Language Testing

1. Switch to French: All inventory labels in French
2. Switch to Arabic: All labels in Arabic, RTL layout correct
3. Switch to English: All labels in English

### Step 22: Fix Any Issues Found

Common issues to watch for:
- 404 on API calls → check route paths match between frontend store and backend controller
- UUID parse error → verify /dashboard and /low-stock routes are before /{id} in controller
- Empty categories → verify seed logic runs on first GET /categories
- Movement not updating stock → verify StockMovementService modifies item.currentQuantity and saves
- i18n key showing raw key → check underscore conversion (nav.group.inventory → nav_group_inventory)

### Final Build Verification
  cd apps/backend && ./mvnw compile && ./mvnw test
  cd apps/frontend && npx vite build

Both must pass with zero errors.
```

---

## Quick Reference — Which Prompt to Use When

| Day | Phase | What You'll Build |
|-----|-------|-------------------|
| Day 1 | **Phase 1** | Migration, Enums, Entities, Repositories |
| Day 2 | **Phase 2** | DTOs, CategoryService, ItemService |
| Day 3 | **Phase 2** (cont.) | StockMovementService |
| Day 4 | **Phase 3** + **Phase 4** | Controllers + Alert trigger + Auto-expense |
| Day 5 | **Phase 4** (cont.) | Unit tests |
| Day 6–9 | **Phase 5** | Frontend views (Form, Detail, Movements, Dashboard connect) |
| Day 10 | **Phase 6** | Integration testing + Polish |
