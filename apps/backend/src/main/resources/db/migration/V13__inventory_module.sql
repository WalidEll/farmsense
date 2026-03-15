-- ── Inventory Module ────────────────────────────────────────────────────────────────
-- Track inventory items (chicks, vaccines, feed supplements, equipment, etc.)

CREATE TABLE inventory_items (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id               UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    team_id               UUID REFERENCES teams(id) ON DELETE SET NULL,
    sku                   VARCHAR(100) UNIQUE NOT NULL,
    name                  VARCHAR(255) NOT NULL,
    name_ar               VARCHAR(255),
    name_en               VARCHAR(255),
    category              VARCHAR(100) NOT NULL, -- CHICKS, VACCINES, FEED, MEDICINE, EQUIPMENT, SUPPLEMENTS, OTHER
    sub_category          VARCHAR(100),
    description           TEXT,
    unit_of_measure       VARCHAR(20) NOT NULL DEFAULT 'pcs', -- pcs, kg, liters, boxes, packs
    current_quantity      NUMERIC(12,2) NOT NULL DEFAULT 0,
    min_quantity          NUMERIC(12,2) NOT NULL DEFAULT 0,
    max_quantity          NUMERIC(12,2),
    reorder_level         NUMERIC(12,2),
    unit_cost             NUMERIC(12,2),
    purchase_price        NUMERIC(12,2),
    sale_price            NUMERIC(12,2),
    expiry_date           DATE,
    batch_number          VARCHAR(100),
    serial_numbers        TEXT[], -- Array of serial numbers for tracked items
    location              VARCHAR(255),
    supplier_id           UUID REFERENCES suppliers(id) ON DELETE SET NULL,
    is_tracked            BOOLEAN NOT NULL DEFAULT FALSE, -- Track individual serial numbers
    is_active             BOOLEAN NOT NULL DEFAULT TRUE,
    notes                 TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Indexes ─────────────────────────────────────────────────────────────────────────

CREATE INDEX idx_inventory_items_user ON inventory_items(user_id);
CREATE INDEX idx_inventory_items_team ON inventory_items(team_id);
CREATE INDEX idx_inventory_items_sku ON inventory_items(sku);
CREATE INDEX idx_inventory_items_category ON inventory_items(category);
CREATE INDEX idx_inventory_items_supplier ON inventory_items(supplier_id);
CREATE INDEX idx_inventory_items_status ON inventory_items(is_active);
CREATE INDEX idx_inventory_items_expiry ON inventory_items(expiry_date);
CREATE INDEX idx_inventory_items_location ON inventory_items(location);
CREATE INDEX idx_inventory_items_reorder ON inventory_items(reorder_level);
