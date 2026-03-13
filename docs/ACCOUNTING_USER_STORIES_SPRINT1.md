# Smart Farm Accounting — Sprint 1 User Stories

> **Module:** Smart Farm Accounting
> **Sprint:** 1 — Core Accounting
> **Date:** 2026-03-13
> **Total endpoints:** 35 (across 5 controllers)
> **Scope:** V12 migration, Transactions CRUD, Tags, Receipt OCR, Labor tracking, Teams, Approval workflow, Dashboard

---

## US-1: V12 Database Migration

**As a** developer
**I want** the accounting schema deployed via Flyway V12 migration
**So that** V11 financial tables are replaced with the unified transaction model and all Sprint 1 entities have backing tables

### Acceptance Criteria

- [ ] V12 migration drops V11 tables: `expenses` (+ 5 indexes), `incomes` (+ 5 indexes)
- [ ] Creates 8 new tables: `teams`, `team_members`, `tags`, `receipts`, `transactions`, `transaction_tags`, `labor_logs`, `approval_logs`
- [ ] Alters `suppliers` and `customers` — adds `team_id UUID REFERENCES teams(id)`
- [ ] `transactions` has: type VARCHAR(10), category VARCHAR(255) (free text), amount DECIMAL(12,2), transaction_date DATE, approval_status DEFAULT 'APPROVED'
- [ ] `transactions` has optional FKs: supplier_id, customer_id, flock_id, crop_plan_id, farm_location_id, receipt_id
- [ ] `team_members` has UNIQUE(team_id, user_id), role DEFAULT 'VIEWER', status DEFAULT 'PENDING'
- [ ] `tags` has UNIQUE(user_id, name), color DEFAULT '#6B7280'
- [ ] `receipts` has ocr_status DEFAULT 'PENDING', ocr_line_items as JSONB
- [ ] `labor_logs` has transaction_id FK for auto-expense linking
- [ ] `approval_logs` tracks action/user/comment per transaction
- [ ] `transaction_tags` has composite PK (transaction_id, tag_id)
- [ ] ~30 indexes on all FKs, date columns, status columns, type, category
- [ ] Backend starts with `ddl-auto: validate` without errors
- [ ] 7 backend enums compile: `TransactionType`, `ApprovalStatus`, `PaymentMethod`, `OcrStatus`, `TeamRole`, `MemberStatus`, `ApprovalAction`

### Implementation Notes

**Files to create:**
- `apps/backend/src/main/resources/db/migration/V12__accounting_module.sql`
- `apps/backend/src/main/java/ma/farmsense/entity/TransactionType.java` — EXPENSE, INCOME
- `apps/backend/src/main/java/ma/farmsense/entity/ApprovalStatus.java` — DRAFT, PENDING, APPROVED, REJECTED
- `apps/backend/src/main/java/ma/farmsense/entity/PaymentMethod.java` — CASH, BANK_TRANSFER, CHECK, MOBILE, OTHER
- `apps/backend/src/main/java/ma/farmsense/entity/OcrStatus.java` — PENDING, PROCESSED, FAILED
- `apps/backend/src/main/java/ma/farmsense/entity/TeamRole.java` — OWNER, MANAGER, VIEWER
- `apps/backend/src/main/java/ma/farmsense/entity/MemberStatus.java` — PENDING, ACTIVE, REVOKED
- `apps/backend/src/main/java/ma/farmsense/entity/ApprovalAction.java` — SUBMITTED, APPROVED, REJECTED

**Tables overview:**

| Table | Owner FK | Key Columns |
|-------|----------|-------------|
| `teams` | `owner_id → users` | name, description |
| `team_members` | `team_id + user_id` | role, status, invited_by, invited_at, joined_at |
| `tags` | `user_id` | name VARCHAR(100), color VARCHAR(7) |
| `receipts` | `user_id`, `team_id` | original_filename, file_path, content_type, file_size_bytes, ocr_vendor/date/amount/category/line_items/raw_json/confidence/status, transaction_id |
| `transactions` | `user_id`, `team_id` | type, category, subcategory, amount, quantity, unit_price, transaction_date, description, payment_method, reference_number, approval_status, approved_by/at, supplier_id, customer_id, receipt_id, flock_id, crop_plan_id, farm_location_id, notes |
| `transaction_tags` | junction | transaction_id + tag_id (composite PK) |
| `labor_logs` | `user_id`, `team_id` | worker_name, worker_role, hourly_rate, hours_worked, work_date, activity, transaction_id, flock_id, crop_plan_id, farm_location_id, notes |
| `approval_logs` | `transaction_id` | action, user_id, comment |

**Reference:** `apps/backend/src/main/resources/db/migration/V11__poultry_module.sql`

---

## US-2: Transaction Management (CRUD + Filtering)

**As a** farmer
**I want** to record income and expense transactions with free-text categories
**So that** I can track all financial activity across any farm type

### Acceptance Criteria

- [ ] `POST /api/v1/transactions` creates a transaction (201) with: type* (EXPENSE/INCOME), category* (free text string), amount*, transactionDate*, optional: subcategory, quantity, unitPrice, description, supplierId, customerId, paymentMethod, referenceNumber, receiptId, flockId, cropPlanId, farmLocationId, tagIds[], notes
- [ ] `GET /api/v1/transactions` returns paginated list for authenticated user, ordered by transactionDate DESC
- [ ] `GET /api/v1/transactions?type=EXPENSE&category=Feed&from=2026-01-01&to=2026-03-31&approvalStatus=APPROVED&tagIds=uuid1,uuid2` filters work
- [ ] `GET /api/v1/transactions/{id}` returns transaction with tags, supplier name, customer name
- [ ] `PUT /api/v1/transactions/{id}` updates transaction fields + tag associations
- [ ] `DELETE /api/v1/transactions/{id}` deletes the transaction (204)
- [ ] `GET /api/v1/transactions/categories` returns distinct category strings for autocomplete
- [ ] Tags resolved by ID on create/update, stored via `transaction_tags` junction
- [ ] Supplier/customer/flock/cropPlan/farmLocation resolved by optional IDs
- [ ] Frontend: `/accounting/transactions` page with filter bar + transaction table
- [ ] Frontend: `/accounting/transactions/new` and `/accounting/transactions/:id/edit` form pages
- [ ] Category field uses autocomplete from `/categories` endpoint
- [ ] All text uses i18n keys (FR/AR/EN)

### Implementation Notes

**Backend files:**
- `apps/backend/src/main/java/ma/farmsense/entity/Transaction.java`
- `apps/backend/src/main/java/ma/farmsense/repository/TransactionRepository.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/CreateTransactionRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/UpdateTransactionRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/TransactionResponse.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/TransactionFilterRequest.java`
- `apps/backend/src/main/java/ma/farmsense/service/TransactionService.java`
- `apps/backend/src/main/java/ma/farmsense/controller/TransactionController.java`

**Frontend files:**
- `apps/frontend/src/stores/accounting.store.ts`
- `apps/frontend/src/views/accounting/TransactionsView.vue`
- `apps/frontend/src/views/accounting/TransactionFormView.vue`
- `apps/frontend/src/components/accounting/TransactionTable.vue`
- `apps/frontend/src/components/accounting/TransactionCard.vue`
- `apps/frontend/src/components/accounting/CategoryAutocomplete.vue`
- `apps/frontend/src/components/accounting/TransactionFilters.vue`
- `apps/frontend/src/components/accounting/DateRangeFilter.vue`

**Entity fields:** id (UUID), user (ManyToOne), team (ManyToOne, optional), type (TransactionType, not null), category (String, not null), subcategory (String), amount (BigDecimal, not null), quantity (Double), unitPrice (BigDecimal), transactionDate (LocalDate, not null), description (String), supplier (ManyToOne), customer (ManyToOne), paymentMethod (PaymentMethod), referenceNumber (String), receipt (OneToOne), approvalStatus (ApprovalStatus, default APPROVED), approvedBy (User), approvedAt (Instant), flock (ManyToOne), cropPlan (ManyToOne), farmLocation (ManyToOne), notes (TEXT), tags (ManyToMany via transaction_tags), createdAt, updatedAt

**Repository methods:**
- `findByUserOrderByTransactionDateDesc(User)` — default list
- Custom `@Query` with optional filters: type, category, date range, approvalStatus
- `@Query("SELECT DISTINCT t.category FROM Transaction t WHERE t.user = :user ORDER BY t.category")` — for autocomplete
- Aggregation queries for dashboard (see US-7)

**Key service behaviors:**
- `create()`: resolves tag IDs → Tag entities, resolves optional supplier/customer/flock/cropPlan/farmLocation by ID
- `update()`: replaces tag set, updates resolved entities
- Team access: if user has a team, check membership with appropriate role

**Reference patterns:**
- Entity: `apps/backend/src/main/java/ma/farmsense/entity/CropPlan.java`
- Service: `apps/backend/src/main/java/ma/farmsense/service/CropPlanService.java`
- Controller: `apps/backend/src/main/java/ma/farmsense/controller/CropPlanController.java`
- Store: `apps/frontend/src/stores/cropPlans.store.ts`

---

## US-3: Custom Tags

**As a** farmer
**I want** to create colored tags and attach them to transactions
**So that** I can organize and filter my financial records flexibly

### Acceptance Criteria

- [ ] `POST /api/v1/tags` creates a tag (201) with: name* (max 100 chars), color (hex, default #6B7280)
- [ ] Tag names are unique per user — duplicate returns 409
- [ ] `GET /api/v1/tags` returns all tags for user ordered by name ASC
- [ ] `PUT /api/v1/tags/{id}` updates name and/or color
- [ ] `DELETE /api/v1/tags/{id}` deletes the tag (204) — removes from all transaction_tags associations
- [ ] Frontend: `/accounting/tags` page with tag grid, inline create/edit, color picker
- [ ] Frontend: TagPicker component for use in TransactionFormView
- [ ] Frontend: TagChip component for display in transaction lists

### Implementation Notes

**Backend files:**
- `apps/backend/src/main/java/ma/farmsense/entity/Tag.java`
- `apps/backend/src/main/java/ma/farmsense/repository/TagRepository.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/CreateTagRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/UpdateTagRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/TagResponse.java`
- `apps/backend/src/main/java/ma/farmsense/service/TagService.java`
- `apps/backend/src/main/java/ma/farmsense/controller/TagController.java`

**Frontend files:**
- `apps/frontend/src/stores/tags.store.ts`
- `apps/frontend/src/views/accounting/TagsManagerView.vue`
- `apps/frontend/src/components/accounting/TagPicker.vue`
- `apps/frontend/src/components/accounting/TagChip.vue`

**Entity fields:** id (UUID), user (ManyToOne, not null), name (String, not null, max 100), color (String, default "#6B7280"), createdAt, updatedAt. `@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))`

**Repository methods:**
- `findByUserOrderByNameAsc(User)`
- `findByUserAndName(User, String)` — for uniqueness check

---

## US-4: AI Receipt Scanner

**As a** farmer
**I want** to upload a receipt photo and have AI extract the financial data
**So that** I can quickly create transactions without manual data entry

### Acceptance Criteria

- [ ] `POST /api/v1/receipts/upload` accepts a multipart image file (201), saves to `uploads/receipts/{userId}/`, creates Receipt with ocrStatus=PENDING
- [ ] OCR processes asynchronously via `@Async` — calls Claude Vision API to extract: date, amount, category, vendor, line items
- [ ] `GET /api/v1/receipts` returns all receipts for user with OCR status
- [ ] `GET /api/v1/receipts/{id}` returns receipt with OCR results (for polling)
- [ ] `POST /api/v1/receipts/{id}/confirm` creates a Transaction from OCR data (optionally with user edits)
- [ ] Frontend: `/accounting/receipts` page with drop zone upload area
- [ ] Frontend: After upload, shows image preview side-by-side with editable OCR results
- [ ] Frontend: Confirm button creates the transaction
- [ ] Frontend: Polls receipt status until OCR completes (PENDING → PROCESSED/FAILED)
- [ ] Supported image types: JPEG, PNG, WebP (max 10MB — existing SecurityConfig limit)

### Implementation Notes

**Backend files:**
- `apps/backend/src/main/java/ma/farmsense/entity/Receipt.java`
- `apps/backend/src/main/java/ma/farmsense/repository/ReceiptRepository.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/ReceiptUploadResponse.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/ReceiptConfirmRequest.java`
- `apps/backend/src/main/java/ma/farmsense/service/ReceiptService.java`
- `apps/backend/src/main/java/ma/farmsense/controller/ReceiptController.java`
- `apps/backend/src/main/java/ma/farmsense/config/AsyncConfig.java`

**Frontend files:**
- `apps/frontend/src/stores/receipts.store.ts`
- `apps/frontend/src/views/accounting/ReceiptScannerView.vue`
- `apps/frontend/src/components/accounting/ReceiptDropZone.vue`
- `apps/frontend/src/components/accounting/OcrResultsForm.vue`

**Entity fields:** id (UUID), user (ManyToOne), team (ManyToOne, optional), originalFilename (String), filePath (String), contentType (String), fileSizeBytes (Long), ocrVendor (String), ocrDate (LocalDate), ocrAmount (BigDecimal), ocrCategory (String), ocrLineItems (String/TEXT — JSON array), ocrRawJson (String/TEXT), ocrConfidence (Double), ocrStatus (OcrStatus, default PENDING), transaction (OneToOne, optional), createdAt, updatedAt

**Repository methods:**
- `findByUserOrderByCreatedAtDesc(User)`
- `findByUserAndOcrStatus(User, OcrStatus)`

**Key service behaviors:**
- `upload()`: saves file to disk, persists Receipt entity, triggers `@Async processOcr(receiptId)`
- `processOcr()`: reads file, base64 encodes, calls Claude Vision API (reuse `DiagnoseService.java` pattern with WebClient), parses response, updates Receipt with OCR fields, sets status to PROCESSED or FAILED
- `confirm()`: takes ReceiptConfirmRequest (user-edited OCR data), creates Transaction, links receipt to transaction
- Claude Vision prompt: extract date, total amount, vendor/category, line items as JSON

**AsyncConfig.java:**
```java
@Configuration @EnableAsync
public class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("ocr-");
        executor.initialize();
        return executor;
    }
}
```

**Reference:** `apps/backend/src/main/java/ma/farmsense/service/DiagnoseService.java` (Claude Vision API pattern), `apps/backend/src/main/java/ma/farmsense/controller/DiagnoseController.java` (multipart upload pattern)

---

## US-5: Labor Tracking

**As a** farmer
**I want** to log labor hours and have them automatically create expense transactions
**So that** I can track worker costs without double-entering data

### Acceptance Criteria

- [ ] `POST /api/v1/labor` creates a labor log (201) with: workerName*, workerRole, hourlyRate*, hoursWorked*, workDate*, activity, flockId, cropPlanId, farmLocationId, notes
- [ ] On create, auto-creates an EXPENSE transaction: category="Labor", amount=hourlyRate*hoursWorked, transactionDate=workDate, description="Labor: {workerName} - {activity}"
- [ ] Labor log stores the auto-created transaction_id as a link
- [ ] `GET /api/v1/labor` returns all labor logs for user ordered by workDate DESC
- [ ] `GET /api/v1/labor?from=2026-01-01&to=2026-03-31` filters by date range
- [ ] `GET /api/v1/labor/{id}` returns single labor log
- [ ] `PUT /api/v1/labor/{id}` updates labor log AND syncs linked transaction (recalculates amount)
- [ ] `DELETE /api/v1/labor/{id}` deletes labor log AND linked transaction (204)
- [ ] Frontend: `/accounting/labor` page with quick entry form + labor log table + monthly summary
- [ ] Frontend: Monthly summary shows total hours and total cost

### Implementation Notes

**Backend files:**
- `apps/backend/src/main/java/ma/farmsense/entity/LaborLog.java`
- `apps/backend/src/main/java/ma/farmsense/repository/LaborLogRepository.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/CreateLaborLogRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/UpdateLaborLogRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/LaborLogResponse.java`
- `apps/backend/src/main/java/ma/farmsense/service/LaborService.java`
- `apps/backend/src/main/java/ma/farmsense/controller/LaborController.java`

**Frontend files:**
- `apps/frontend/src/stores/labor.store.ts`
- `apps/frontend/src/views/accounting/LaborTrackingView.vue`
- `apps/frontend/src/components/accounting/LaborLogForm.vue`
- `apps/frontend/src/components/accounting/LaborLogTable.vue`

**Entity fields:** id (UUID), user (ManyToOne), team (ManyToOne, optional), workerName (String, not null), workerRole (String), hourlyRate (BigDecimal, not null), hoursWorked (Double, not null), workDate (LocalDate, not null), activity (String), transaction (ManyToOne — auto-created expense), flock (ManyToOne, optional), cropPlan (ManyToOne, optional), farmLocation (ManyToOne, optional), notes (TEXT), createdAt, updatedAt

**Repository methods:**
- `findByUserOrderByWorkDateDesc(User)`
- `findByUserAndWorkDateBetween(User, LocalDate, LocalDate)`

**Key service behaviors:**
- `create()`: creates LaborLog, then auto-creates Transaction(type=EXPENSE, category="Labor", amount=hourlyRate*hoursWorked, transactionDate=workDate), links transaction to labor log
- `update()`: updates LaborLog fields, recalculates and updates linked Transaction amount
- `delete()`: deletes both LaborLog and linked Transaction

---

## US-6: Team Management & Invitations

**As a** farm owner
**I want** to create a team and invite others with specific roles
**So that** managers can help record transactions and viewers can see financial reports

### Acceptance Criteria

- [ ] `POST /api/v1/teams` creates a team (201) with: name*, description
- [ ] Creator is automatically added as OWNER in team_members
- [ ] `GET /api/v1/teams` returns teams where user is owner or member
- [ ] `PUT /api/v1/teams/{id}` updates team name/description (OWNER only)
- [ ] `DELETE /api/v1/teams/{id}` deletes team + all memberships (OWNER only, 204)
- [ ] `POST /api/v1/teams/{id}/invite` invites a user by email with role (OWNER/MANAGER/VIEWER) — creates PENDING team_member
- [ ] `GET /api/v1/teams/{id}/members` returns team member list with roles and statuses
- [ ] `POST /api/v1/teams/{id}/accept` — invited user accepts, status → ACTIVE
- [ ] `DELETE /api/v1/teams/{id}/members/{memberId}` removes a member (OWNER only)
- [ ] Role enforcement: VIEWER can only read, MANAGER can create/edit transactions, OWNER can do everything
- [ ] Frontend: `/accounting/team` page with team list, create modal, member list, invite form
- [ ] Frontend: PendingInviteBanner shows pending invitations to the user

### Implementation Notes

**Backend files:**
- `apps/backend/src/main/java/ma/farmsense/entity/Team.java`
- `apps/backend/src/main/java/ma/farmsense/entity/TeamMember.java`
- `apps/backend/src/main/java/ma/farmsense/repository/TeamRepository.java`
- `apps/backend/src/main/java/ma/farmsense/repository/TeamMemberRepository.java`
- `apps/backend/src/main/java/ma/farmsense/dto/team/CreateTeamRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/team/UpdateTeamRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/team/TeamResponse.java`
- `apps/backend/src/main/java/ma/farmsense/dto/team/InviteMemberRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/team/TeamMemberResponse.java`
- `apps/backend/src/main/java/ma/farmsense/service/TeamService.java`
- `apps/backend/src/main/java/ma/farmsense/controller/TeamController.java`

**Frontend files:**
- `apps/frontend/src/stores/teams.store.ts`
- `apps/frontend/src/views/accounting/TeamManagementView.vue`
- `apps/frontend/src/components/accounting/TeamCard.vue`
- `apps/frontend/src/components/accounting/InviteMemberDialog.vue`
- `apps/frontend/src/components/accounting/PendingInviteBanner.vue`

**Entity fields (Team):** id (UUID), owner (ManyToOne User, not null), name (String, not null), description (String), createdAt, updatedAt

**Entity fields (TeamMember):** id (UUID), team (ManyToOne, not null), user (ManyToOne, not null), role (TeamRole, default VIEWER), invitedBy (ManyToOne User), invitedAt (Instant), joinedAt (Instant), status (MemberStatus, default PENDING), createdAt, updatedAt. `@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "user_id"}))`

**Repository methods (Team):**
- `findByOwner(User)`
- `findByIdAndOwner(UUID, User)`

**Repository methods (TeamMember):**
- `findByTeam(Team)`
- `findByUserAndStatus(User, MemberStatus)`
- `findByTeamAndUser(Team, User)`

**Key service behaviors:**
- `createTeam()`: creates Team, auto-creates TeamMember(role=OWNER, status=ACTIVE) for creator
- `invite()`: finds user by email, creates TeamMember(status=PENDING, invitedBy=currentUser)
- `accept()`: finds PENDING membership for current user, sets status=ACTIVE, joinedAt=now
- `requireRole()`: helper that checks if user has minimum role (VIEWER < MANAGER < OWNER) — throws 403 if insufficient

---

## US-7: Transaction Approval Workflow

**As a** team owner/manager
**I want** to approve or reject transactions submitted by team members
**So that** I can maintain oversight of farm finances

### Acceptance Criteria

- [ ] When a team exists, new transactions from VIEWER/MANAGER role get approvalStatus=PENDING (not APPROVED)
- [ ] OWNER transactions default to APPROVED
- [ ] `POST /api/v1/transactions/{id}/approve` — OWNER/MANAGER can approve (sets status=APPROVED, approvedBy, approvedAt) or reject
- [ ] `GET /api/v1/transactions/{id}/approval-history` returns chronological list of approval actions
- [ ] Each approve/reject creates an ApprovalLog entry with action, user, optional comment
- [ ] Dashboard shows pending approvals count as a KPI
- [ ] Frontend: ApprovalBadge component shows status (color-coded: green=APPROVED, yellow=PENDING, red=REJECTED)
- [ ] Frontend: ApprovalDialog for approve/reject with optional comment

### Implementation Notes

**Backend files:**
- `apps/backend/src/main/java/ma/farmsense/entity/ApprovalLog.java`
- `apps/backend/src/main/java/ma/farmsense/repository/ApprovalLogRepository.java`
- `apps/backend/src/main/java/ma/farmsense/dto/team/ApprovalRequest.java`
- `apps/backend/src/main/java/ma/farmsense/dto/team/ApprovalLogResponse.java`

**Frontend files:**
- `apps/frontend/src/components/accounting/ApprovalBadge.vue`
- `apps/frontend/src/components/accounting/ApprovalDialog.vue`

**Entity fields:** id (UUID), transaction (ManyToOne, not null), action (ApprovalAction, not null), user (ManyToOne, not null), comment (TEXT), createdAt

**Repository methods:**
- `findByTransactionOrderByCreatedAtDesc(Transaction)`

**Key service behaviors (in TransactionService):**
- `approve()`: validates caller has OWNER or MANAGER role, updates transaction.approvalStatus, sets approvedBy/approvedAt, creates ApprovalLog entry
- `create()`: if user has a team and role is not OWNER, set approvalStatus=PENDING instead of APPROVED

---

## US-8: Accounting Dashboard

**As a** farmer
**I want** a dashboard showing my financial KPIs, category breakdowns, and recent activity
**So that** I can quickly understand my farm's financial health

### Acceptance Criteria

- [ ] `GET /api/v1/transactions/dashboard?from=2026-01-01&to=2026-03-31` returns aggregated data
- [ ] Response includes: totalIncome, totalExpenses, netProfit (income - expenses)
- [ ] Response includes: expensesByCategory (list of {category, total}), incomesByCategory (same)
- [ ] Response includes: recentTransactions (last 10), pendingApprovals count
- [ ] Frontend: `/accounting` dashboard with 4 KPI cards (Income, Expenses, Net Profit, Pending Approvals)
- [ ] Frontend: Date range filter (this month / this quarter / this year / custom)
- [ ] Frontend: Category doughnut chart for expenses
- [ ] Frontend: Income vs Expense bar chart (monthly)
- [ ] Frontend: Recent transactions list

### Implementation Notes

**Backend files:**
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/DashboardResponse.java`
- `apps/backend/src/main/java/ma/farmsense/dto/accounting/CategorySummary.java`

**Frontend files:**
- `apps/frontend/src/views/accounting/AccountingDashboardView.vue`
- `apps/frontend/src/components/accounting/DashboardKpiCard.vue`
- `apps/frontend/src/components/accounting/CategoryChart.vue`
- `apps/frontend/src/components/accounting/IncomeExpenseChart.vue`

**DashboardResponse fields:** totalIncome (BigDecimal), totalExpenses (BigDecimal), netProfit (BigDecimal), expensesByCategory (List<CategorySummary>), incomesByCategory (List<CategorySummary>), recentTransactions (List<TransactionResponse>), pendingApprovals (long)

**CategorySummary fields:** category (String), total (BigDecimal)

**Repository queries (in TransactionRepository):**
- `@Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.transactionDate BETWEEN :from AND :to AND t.approvalStatus = 'APPROVED'")`
- `@Query("SELECT new ma.farmsense.dto.accounting.CategorySummary(t.category, SUM(t.amount)) FROM Transaction t WHERE t.user = :user AND t.type = :type AND t.transactionDate BETWEEN :from AND :to GROUP BY t.category ORDER BY SUM(t.amount) DESC")`
- `countByUserAndApprovalStatus(User, ApprovalStatus)` — for pending count

**Reference:** `apps/frontend/src/views/DashboardView.vue` (KPI cards pattern), `apps/frontend/src/components/sensors/SensorChart.vue` (Chart.js pattern)

---

## US-9: Navigation, Routes & Sidebar

**As a** farmer
**I want** a dedicated Accounting section in the sidebar
**So that** I can quickly access all financial features

### Acceptance Criteria

- [ ] Sidebar has a new "Comptabilite" section
- [ ] Sidebar links: Dashboard, Transactions, Receipt Scanner, Labor, Tags, Team
- [ ] Active state highlights correctly for all accounting routes
- [ ] 8 routes registered and lazy-loaded under `/accounting`
- [ ] All navigation text uses i18n (FR/AR/EN)

### Implementation Notes

**Modified files:**
- `apps/frontend/src/router/index.ts` — add 8 routes as children of `/` layout
- `apps/frontend/src/components/shared/SideBar.vue` — add Comptabilite section
- `apps/frontend/src/i18n/index.ts` — add ~80 keys across FR/AR/EN

**Routes:**

| Path | Name | Component |
|------|------|-----------|
| `accounting` | `accounting-dashboard` | `AccountingDashboardView.vue` |
| `accounting/transactions` | `transactions` | `TransactionsView.vue` |
| `accounting/transactions/new` | `transaction-new` | `TransactionFormView.vue` |
| `accounting/transactions/:id/edit` | `transaction-edit` | `TransactionFormView.vue` |
| `accounting/receipts` | `receipt-scanner` | `ReceiptScannerView.vue` |
| `accounting/labor` | `labor-tracking` | `LaborTrackingView.vue` |
| `accounting/tags` | `tags-manager` | `TagsManagerView.vue` |
| `accounting/team` | `team-management` | `TeamManagementView.vue` |

**Sidebar section:**
- Dashboard → `/accounting`
- Transactions → `/accounting/transactions`
- Receipt Scanner → `/accounting/receipts`
- Labor → `/accounting/labor`
- Tags → `/accounting/tags`
- Team → `/accounting/team`

**i18n key groups:**

| Group | Keys |
|-------|------|
| Navigation | `nav_accounting`, `nav_accounting_dashboard`, `nav_transactions`, `nav_receipts`, `nav_labor`, `nav_tags`, `nav_team` |
| Transactions | `transactions_title`, `transaction_create`, `transaction_type`, `transaction_category`, `transaction_amount`, `transaction_date`, `transaction_description`, `transaction_expense`, `transaction_income`, `transaction_subcategory`, `transaction_quantity`, `transaction_unit_price`, `transaction_payment_method`, `transaction_reference`, `transaction_notes` |
| Approval | `approval_draft`, `approval_pending`, `approval_approved`, `approval_rejected`, `approval_approve`, `approval_reject`, `approval_comment`, `approval_history` |
| Payment | `payment_cash`, `payment_bank_transfer`, `payment_check`, `payment_mobile`, `payment_other` |
| Dashboard | `dashboard_total_income`, `dashboard_total_expenses`, `dashboard_net_profit`, `dashboard_pending_approvals`, `dashboard_expenses_by_category`, `dashboard_income_vs_expense`, `dashboard_recent_transactions` |
| Receipts | `receipt_upload`, `receipt_scan`, `receipt_confirm`, `receipt_processing`, `receipt_failed`, `receipt_date`, `receipt_amount`, `receipt_vendor`, `receipt_line_items` |
| Labor | `labor_title`, `labor_worker_name`, `labor_worker_role`, `labor_hourly_rate`, `labor_hours_worked`, `labor_work_date`, `labor_activity`, `labor_monthly_summary`, `labor_total_hours`, `labor_total_cost` |
| Tags | `tags_title`, `tags_create`, `tags_name`, `tags_color`, `tags_empty` |
| Team | `team_title`, `team_create`, `team_invite`, `team_accept`, `team_members`, `team_role_owner`, `team_role_manager`, `team_role_viewer`, `team_pending_invite`, `team_remove_member` |

**Reference:** `apps/frontend/src/components/shared/SideBar.vue`, `apps/frontend/src/router/index.ts`

---

## US-10: Frontend Types & Stores

**As a** developer
**I want** TypeScript types and Pinia stores for the accounting module
**So that** all views have type-safe data access and state management

### Acceptance Criteria

- [ ] Enum types added: `TransactionType`, `ApprovalStatus`, `PaymentMethod`, `OcrStatus`, `TeamRole`, `MemberStatus`, `ApprovalAction`
- [ ] Interfaces added: `Transaction`, `Tag`, `Receipt`, `LaborLog`, `Team`, `TeamMember`, `ApprovalLogEntry`, `DashboardData`, `CategorySummary`
- [ ] Request types added: `CreateTransactionRequest`, `UpdateTransactionRequest`, `TransactionFilter`, `CreateTagRequest`, `ReceiptConfirmRequest`, `CreateLaborLogRequest`, `CreateTeamRequest`, `InviteMemberRequest`, `ApprovalRequest`
- [ ] 5 Pinia stores created: `accounting.store.ts`, `tags.store.ts`, `receipts.store.ts`, `labor.store.ts`, `teams.store.ts`
- [ ] Stores use `api.get/post/put/delete` (NOT `.then(r => r.data)` — api wrapper already extracts data)
- [ ] `npm run type-check` passes with no errors

### Implementation Notes

**Modified file:** `apps/frontend/src/types/index.ts` — append accounting types

**New files:**
- `apps/frontend/src/stores/accounting.store.ts`
- `apps/frontend/src/stores/tags.store.ts`
- `apps/frontend/src/stores/receipts.store.ts`
- `apps/frontend/src/stores/labor.store.ts`
- `apps/frontend/src/stores/teams.store.ts`

**Store structure (each):**
- `defineStore('name', () => { ... })` — Composition API style
- State: `ref<T[]>`, `ref<T | null>`, `ref(false)` for loading
- CRUD actions: fetch, create, update, delete
- `receipts.store.ts` additionally: `uploadReceipt`, `pollOcrStatus`, `confirmReceipt`
- `accounting.store.ts` additionally: `fetchDashboard`, `fetchCategories`, `approveTransaction`
- `teams.store.ts` additionally: `inviteMember`, `acceptInvite`, `removeMember`

**Critical:** `api.get()` already returns `response.data` — do NOT chain `.then(r => r.data)`.

**Reference:** `apps/frontend/src/stores/cropPlans.store.ts`, `apps/frontend/src/services/api.ts`

---

## US-11: Modified Entities (Supplier & Customer)

**As a** developer
**I want** the existing Supplier and Customer entities to support team association
**So that** suppliers and customers can be shared across team members

### Acceptance Criteria

- [ ] `Supplier.java` has new field: `@ManyToOne Team team` with `@JoinColumn(name = "team_id")`
- [ ] `Customer.java` has same team field
- [ ] V12 migration adds `team_id` column to both `suppliers` and `customers` tables
- [ ] Existing data is not affected (team_id is nullable)
- [ ] Backend compiles and starts without Hibernate validation errors

### Implementation Notes

**Modified files:**
- `apps/backend/src/main/java/ma/farmsense/entity/Supplier.java` — add `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "team_id") private Team team;`
- `apps/backend/src/main/java/ma/farmsense/entity/Customer.java` — same

---

## Implementation Phases

For agent execution, implement in this order:

### Phase 1: Foundation (US-1, US-11)
V12 migration + 7 enums + modified Supplier/Customer entities. Verify: `./mvnw compile` + `spring-boot:run` with no Hibernate errors.

### Phase 2: Core Backend (US-2, US-3, US-5, US-6, US-7, US-8)
All 7 entities + 7 repositories + 21 DTOs + 5 services + 5 controllers + AsyncConfig. Verify: `./mvnw compile`.

### Phase 3: Frontend Foundation (US-9, US-10)
Types + 5 stores + routes + sidebar + i18n. Verify: `npm run type-check` + `npm run build`.

### Phase 4: Frontend Views (US-2, US-3, US-4, US-5, US-6, US-7, US-8)
7 views + 19 components. Verify: `npm run build` + visual preview.

---

## Verification Checklist

### Backend
1. `cd apps/backend && JAVA_HOME=/Users/walidelalaouy/Library/Java/JavaVirtualMachines/temurin-17.0.9/Contents/Home ./mvnw compile` — passes
2. Start Docker: `cd apps/backend && docker compose up -d`
3. `./mvnw spring-boot:run` — starts without Hibernate validation errors
4. Smoke test API:
   - `POST /api/v1/teams` → 201
   - `POST /api/v1/tags` → 201
   - `POST /api/v1/transactions` → 201 (expense with free-text category + tags)
   - `GET /api/v1/transactions/dashboard` → KPI data
   - `POST /api/v1/receipts/upload` → 201, OCR processes async
   - `POST /api/v1/labor` → 201 + auto-creates expense transaction
   - `POST /api/v1/transactions/{id}/approve` → approval logged

### Frontend
5. `cd apps/frontend && npm run build` — passes
6. `npm run type-check` — passes
7. Preview at `http://localhost:5173`:
   - Sidebar shows "Comptabilite" section
   - `/accounting` dashboard with KPI cards + charts
   - `/accounting/transactions` list with filters
   - `/accounting/transactions/new` form works
   - `/accounting/receipts` upload + OCR display
   - `/accounting/labor` entry + table
   - `/accounting/tags` CRUD
   - `/accounting/team` invite + members

---

## API Contract Reference

See full OpenAPI 3.0 spec: `apps/backend/src/main/resources/openapi/accounting-sprint1.yaml`
