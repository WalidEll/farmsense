-- ── Smart Farm Accounting Module ───────────────────────────────────
-- Sprint 1: Foundation (Transactions, Tags, Receipts, Labor, Teams)

-- 1. Drop V11 financial tables (replacing with unified Transaction model)
DROP TABLE IF EXISTS expenses CASCADE;
DROP TABLE IF EXISTS incomes  CASCADE;

-- 2. Create Teams
CREATE TABLE teams (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. Team Members
CREATE TABLE team_members (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    team_id     UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role        VARCHAR(20) NOT NULL DEFAULT 'VIEWER', -- OWNER, MANAGER, VIEWER
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, ACTIVE, REVOKED
    invited_by  UUID REFERENCES users(id) ON DELETE SET NULL,
    invited_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    joined_at   TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(team_id, user_id)
);

-- 4. Custom Tags
CREATE TABLE tags (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    color       VARCHAR(7) NOT NULL DEFAULT '#6B7280',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, name)
);

-- 5. Receipts (OCR processed)
CREATE TABLE receipts (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    team_id             UUID REFERENCES teams(id) ON DELETE SET NULL,
    original_filename   VARCHAR(255),
    file_path           VARCHAR(512) NOT NULL,
    content_type        VARCHAR(100),
    file_size_bytes     BIGINT,
    ocr_vendor          VARCHAR(50),
    ocr_date            DATE,
    ocr_amount          DECIMAL(12,2),
    ocr_category        VARCHAR(255),
    ocr_line_items      JSONB,
    ocr_raw_json        TEXT,
    ocr_confidence      DOUBLE PRECISION,
    ocr_status          VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, PROCESSED, FAILED
    transaction_id      UUID, -- Linked after confirmation
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 6. Unified Transactions
CREATE TABLE transactions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    team_id             UUID REFERENCES teams(id) ON DELETE SET NULL,
    type                VARCHAR(10) NOT NULL, -- EXPENSE, INCOME
    category            VARCHAR(255) NOT NULL,
    subcategory         VARCHAR(255),
    amount              DECIMAL(12,2) NOT NULL,
    quantity            DOUBLE PRECISION,
    unit_price          DECIMAL(12,2),
    transaction_date    DATE NOT NULL,
    description         TEXT,
    payment_method      VARCHAR(20), -- CASH, BANK_TRANSFER, CHECK, MOBILE, OTHER
    reference_number    VARCHAR(100),
    approval_status     VARCHAR(20) NOT NULL DEFAULT 'APPROVED', -- DRAFT, PENDING, APPROVED, REJECTED
    approved_by         UUID REFERENCES users(id) ON DELETE SET NULL,
    approved_at         TIMESTAMPTZ,
    supplier_id         UUID REFERENCES suppliers(id) ON DELETE SET NULL,
    customer_id         UUID REFERENCES customers(id) ON DELETE SET NULL,
    receipt_id          UUID REFERENCES receipts(id) ON DELETE SET NULL,
    flock_id            UUID REFERENCES flocks(id) ON DELETE SET NULL,
    crop_plan_id        UUID REFERENCES crop_plans(id) ON DELETE SET NULL,
    farm_location_id    UUID REFERENCES farm_locations(id) ON DELETE SET NULL,
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 7. Transaction Tags (Junction)
CREATE TABLE transaction_tags (
    transaction_id      UUID NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    tag_id              UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (transaction_id, tag_id)
);

-- 8. Labor Logs
CREATE TABLE labor_logs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    team_id             UUID REFERENCES teams(id) ON DELETE SET NULL,
    worker_name         VARCHAR(255) NOT NULL,
    worker_role         VARCHAR(255),
    hourly_rate         DECIMAL(12,2) NOT NULL,
    hours_worked        DOUBLE PRECISION NOT NULL,
    work_date           DATE NOT NULL,
    activity            VARCHAR(255),
    transaction_id      UUID REFERENCES transactions(id) ON DELETE SET NULL,
    flock_id            UUID REFERENCES flocks(id) ON DELETE SET NULL,
    crop_plan_id        UUID REFERENCES crop_plans(id) ON DELETE SET NULL,
    farm_location_id    UUID REFERENCES farm_locations(id) ON DELETE SET NULL,
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 9. Approval Logs
CREATE TABLE approval_logs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id      UUID NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    action              VARCHAR(20) NOT NULL, -- SUBMITTED, APPROVED, REJECTED
    comment             TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 10. Alter Suppliers/Customers for Team support
ALTER TABLE suppliers ADD COLUMN team_id UUID REFERENCES teams(id) ON DELETE SET NULL;
ALTER TABLE customers ADD COLUMN team_id UUID REFERENCES teams(id) ON DELETE SET NULL;

-- ── Indexes ───────────────────────────────────────────────────────

CREATE INDEX idx_teams_owner ON teams(owner_id);
CREATE INDEX idx_team_members_team ON team_members(team_id);
CREATE INDEX idx_team_members_user ON team_members(user_id);
CREATE INDEX idx_team_members_status ON team_members(status);

CREATE INDEX idx_tags_user ON tags(user_id);

CREATE INDEX idx_receipts_user ON receipts(user_id);
CREATE INDEX idx_receipts_team ON receipts(team_id);
CREATE INDEX idx_receipts_status ON receipts(ocr_status);

CREATE INDEX idx_transactions_user ON transactions(user_id);
CREATE INDEX idx_transactions_team ON transactions(team_id);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_date ON transactions(transaction_date);
CREATE INDEX idx_transactions_category ON transactions(category);
CREATE INDEX idx_transactions_status ON transactions(approval_status);
CREATE INDEX idx_transactions_supplier ON transactions(supplier_id);
CREATE INDEX idx_transactions_customer ON transactions(customer_id);
CREATE INDEX idx_transactions_flock ON transactions(flock_id);
CREATE INDEX idx_transactions_plan ON transactions(crop_plan_id);
CREATE INDEX idx_transactions_receipt ON transactions(receipt_id);

CREATE INDEX idx_labor_logs_user ON labor_logs(user_id);
CREATE INDEX idx_labor_logs_team ON labor_logs(team_id);
CREATE INDEX idx_labor_logs_date ON labor_logs(work_date);
CREATE INDEX idx_labor_logs_transaction ON labor_logs(transaction_id);

CREATE INDEX idx_approval_logs_transaction ON approval_logs(transaction_id);

CREATE INDEX idx_suppliers_team ON suppliers(team_id);
CREATE INDEX idx_customers_team ON customers(team_id);
