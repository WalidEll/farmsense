-- ── Poultry Farm Management Module ──────────────────────────────────
-- Sprint 1: Foundation (Suppliers, Customers, Flocks)
-- Sprint 2-4 support: Expenses, Incomes, Production, Health

CREATE TABLE suppliers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name                VARCHAR(255) NOT NULL,
    phone               VARCHAR(50),
    email               VARCHAR(255),
    address             TEXT,
    products_supplied   TEXT,
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE customers (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name                VARCHAR(255) NOT NULL,
    phone               VARCHAR(50),
    email               VARCHAR(255),
    address             TEXT,
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE flocks (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    supplier_id         UUID REFERENCES suppliers(id) ON DELETE SET NULL,
    name                VARCHAR(255) NOT NULL,
    name_ar             VARCHAR(255),
    name_en             VARCHAR(255),
    breed               VARCHAR(255),
    bird_count          INTEGER NOT NULL,
    current_bird_count  INTEGER NOT NULL,
    purpose             VARCHAR(20) NOT NULL, -- LAYERS, BROILERS
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, SOLD, FINISHED
    start_date          DATE,
    source              VARCHAR(255),
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE expenses (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    flock_id            UUID REFERENCES flocks(id) ON DELETE SET NULL,
    supplier_id         UUID REFERENCES suppliers(id) ON DELETE SET NULL,
    category            VARCHAR(50) NOT NULL, -- FEED, CHICKS, VACCINE, MEDICINE, EQUIPMENT, LABOR, UTILITIES, OTHER
    amount              DECIMAL(12,2) NOT NULL,
    expense_date        DATE NOT NULL,
    description         TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE incomes (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    flock_id            UUID REFERENCES flocks(id) ON DELETE SET NULL,
    customer_id         UUID REFERENCES customers(id) ON DELETE SET NULL,
    product             VARCHAR(50) NOT NULL, -- EGGS, BIRDS, MANURE, OTHER
    quantity            DOUBLE PRECISION NOT NULL,
    unit_price          DECIMAL(12,2) NOT NULL,
    total_amount        DECIMAL(12,2) NOT NULL,
    income_date         DATE NOT NULL,
    description         TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE egg_collections (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flock_id            UUID NOT NULL REFERENCES flocks(id) ON DELETE CASCADE,
    collection_date     DATE NOT NULL,
    total_eggs          INTEGER NOT NULL,
    broken_eggs         INTEGER NOT NULL DEFAULT 0,
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE feed_records (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flock_id            UUID NOT NULL REFERENCES flocks(id) ON DELETE CASCADE,
    feed_type           VARCHAR(50) NOT NULL, -- STARTER, GROWER, LAYER, FINISHER, OTHER
    quantity_kg         DOUBLE PRECISION NOT NULL,
    feed_date           DATE NOT NULL,
    cost                DECIMAL(12,2),
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE bird_weights (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flock_id            UUID NOT NULL REFERENCES flocks(id) ON DELETE CASCADE,
    sample_size         INTEGER NOT NULL,
    avg_weight_g        DOUBLE PRECISION NOT NULL,
    weigh_date          DATE NOT NULL,
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE mortality_records (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flock_id            UUID NOT NULL REFERENCES flocks(id) ON DELETE CASCADE,
    count               INTEGER NOT NULL,
    mortality_date      DATE NOT NULL,
    cause               VARCHAR(255),
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE vaccination_records (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flock_id            UUID NOT NULL REFERENCES flocks(id) ON DELETE CASCADE,
    vaccine_name        VARCHAR(255) NOT NULL,
    vaccination_date    DATE NOT NULL,
    next_due_date       DATE,
    administered_by     VARCHAR(255),
    notes               TEXT,
    cost                DECIMAL(12,2),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Indexes ────────────────────────────────────────────────────────

CREATE INDEX idx_suppliers_user ON suppliers(user_id);
CREATE INDEX idx_customers_user ON customers(user_id);
CREATE INDEX idx_flocks_user ON flocks(user_id);
CREATE INDEX idx_flocks_supplier ON flocks(supplier_id);
CREATE INDEX idx_flocks_status ON flocks(status);
CREATE INDEX idx_flocks_purpose ON flocks(purpose);

CREATE INDEX idx_expenses_user ON expenses(user_id);
CREATE INDEX idx_expenses_flock ON expenses(flock_id);
CREATE INDEX idx_expenses_supplier ON expenses(supplier_id);
CREATE INDEX idx_expenses_date ON expenses(expense_date);
CREATE INDEX idx_expenses_category ON expenses(category);

CREATE INDEX idx_incomes_user ON incomes(user_id);
CREATE INDEX idx_incomes_flock ON incomes(flock_id);
CREATE INDEX idx_incomes_customer ON incomes(customer_id);
CREATE INDEX idx_incomes_date ON incomes(income_date);
CREATE INDEX idx_incomes_product ON incomes(product);

CREATE INDEX idx_egg_collections_flock ON egg_collections(flock_id);
CREATE INDEX idx_egg_collections_date ON egg_collections(collection_date);

CREATE INDEX idx_feed_records_flock ON feed_records(flock_id);
CREATE INDEX idx_feed_records_date ON feed_records(feed_date);

CREATE INDEX idx_bird_weights_flock ON bird_weights(flock_id);
CREATE INDEX idx_bird_weights_date ON bird_weights(weigh_date);

CREATE INDEX idx_mortality_records_flock ON mortality_records(flock_id);
CREATE INDEX idx_mortality_records_date ON mortality_records(mortality_date);

CREATE INDEX idx_vaccination_records_flock ON vaccination_records(flock_id);
CREATE INDEX idx_vaccination_records_date ON vaccination_records(vaccination_date);
