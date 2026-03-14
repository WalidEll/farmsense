-- ── Stock Movements Module ───────────────────────────────────────────────
-- Track all stock movements (chicks, vaccines, feed supplements, equipment)

CREATE TABLE stock_movements (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    team_id               UUID REFERENCES teams(id) ON DELETE SET NULL,
    inventory_item_id     UUID NOT NULL REFERENCES inventory_items(id) ON DELETE CASCADE,
    type                  VARCHAR(20) NOT NULL CHECK (type IN ('IN','OUT','ADJUSTMENT','TRANSFER','CONSUMPTION','WASTAGE','RETURN','SALE','PURCHASE')),
    reference_number      VARCHAR(100),
    batch_number          VARCHAR(100),
    serial_number         VARCHAR(100),
    quantity              NUMERIC(12,2) NOT NULL,
    unit_price            NUMERIC(12,2),
    total_value           NUMERIC(12,2),
    weight                DOUBLE PRECISION,
    location              VARCHAR(255),
    quality_grade         VARCHAR(20),
    condition             VARCHAR(100),
    movement_date         DATE NOT NULL,
    description           TEXT,
    source                VARCHAR(255),
    destination           VARCHAR(255),
    supplier_id           UUID REFERENCES suppliers(id) ON DELETE SET NULL,
    customer_id           UUID REFERENCES customers(id) ON DELETE SET NULL,
    flock_id              UUID REFERENCES flocks(id) ON DELETE SET NULL,
    reason                VARCHAR(50),
    notes                 TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Indexes ───────────────────────────────────────────────────────────────

CREATE INDEX idx_stock_movements_user ON stock_movements(user_id);
CREATE INDEX idx_stock_movements_inventory_item ON stock_movements(inventory_item_id);
CREATE INDEX idx_stock_movements_date ON stock_movements(movement_date);
CREATE INDEX idx_stock_movements_type ON stock_movements(type);
CREATE INDEX idx_stock_movements_reference ON stock_movements(reference_number);
CREATE INDEX idx_stock_movements_batch ON stock_movements(batch_number);
CREATE INDEX idx_stock_movements_supplier ON stock_movements(supplier_id);
CREATE INDEX idx_stock_movements_customer ON stock_movements(customer_id);
CREATE INDEX idx_stock_movements_flock ON stock_movements(flock_id);
CREATE INDEX idx_stock_movements_reason ON stock_movements(reason);
