package ma.farmsense.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "inventory_items")
@Access(AccessType.FIELD)
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(name = "name_ar")
    private String nameAr;

    @Column(name = "name_en")
    private String nameEn;

    @Column(nullable = false, length = 100)
    private String category; // CHICKS, VACCINES, FEED, MEDICINE, EQUIPMENT, SUPPLEMENTS, OTHER

    @Column(name = "sub_category")
    private String subCategory;

    @Column(length = 500)
    private String description;

    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure = "pcs"; // pcs, kg, liters, boxes, packs

    @Column(name = "current_quantity", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentQuantity;

    @Column(name = "min_quantity", nullable = false, precision = 12, scale = 2)
    private BigDecimal minQuantity;

    @Column(precision = 12, scale = 2)
    private BigDecimal maxQuantity;

    @Column(name = "reorder_level", precision = 12, scale = 2)
    private BigDecimal reorderLevel;

    @Column(precision = 12, scale = 2)
    private BigDecimal unitCost;

    @Column(precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "serial_numbers", columnDefinition = "uuid[]")
    private java.util.UUID[] serialNumbers;

    @Column(length = 255)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "is_tracked", nullable = false)
    private Boolean isTracked = false; // Track individual serial numbers

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamptz")
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return this.userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Team getTeam() { return this.team; }
    public void setTeam(Team team) { this.team = team; }

    public String getSku() { return this.sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getNameAr() { return this.nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getNameEn() { return this.nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getCategory() { return this.category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubCategory() { return this.subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getUnitOfMeasure() { return this.unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public BigDecimal getCurrentQuantity() { return this.currentQuantity; }
    public void setCurrentQuantity(BigDecimal currentQuantity) { this.currentQuantity = currentQuantity; }

    public BigDecimal getMinQuantity() { return this.minQuantity; }
    public void setMinQuantity(BigDecimal minQuantity) { this.minQuantity = minQuantity; }

    public BigDecimal getMaxQuantity() { return this.maxQuantity; }
    public void setMaxQuantity(BigDecimal maxQuantity) { this.maxQuantity = maxQuantity; }

    public BigDecimal getReorderLevel() { return this.reorderLevel; }
    public void setReorderLevel(BigDecimal reorderLevel) { this.reorderLevel = reorderLevel; }

    public BigDecimal getUnitCost() { return this.unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public BigDecimal getPurchasePrice() { return this.purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public BigDecimal getSalePrice() { return this.salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public LocalDate getExpiryDate() { return this.expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getBatchNumber() { return this.batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public UUID[] getSerialNumbers() { return this.serialNumbers; }
    public void setSerialNumbers(UUID[] serialNumbers) { this.serialNumbers = serialNumbers; }

    public String getLocation() { return this.location; }
    public void setLocation(String location) { this.location = location; }

    public Supplier getSupplier() { return this.supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public Boolean getIsTracked() { return this.isTracked; }
    public void setIsTracked(Boolean isTracked) { this.isTracked = isTracked; }

    public Boolean getIsActive() { return this.isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    // ── ENUMS ───────────────────────────────────────────────────────────────────────────

    /**
     * Inventory category classification for different types of farm items
     */
    public enum Category {
        CHICKS,          // Baby chicks for sale (starter birds)
        VACCINES,        // Vaccines and immunizations
        FEED,            // Animal feed supplements and additives
        MEDICINE,        // Veterinary medicines and treatments
        EQUIPMENT,       // Farm equipment and tools
        SUPPLEMENTS,     // Feed additives and nutritional supplements
        OTHER            // Other inventory items that don't fit above categories
    }

    /**
     * Common units of measure for inventory items
     */
    public enum UnitOfMeasure {
        PCS("pcs"),
        KG("kg"),
        LITERS("liters"),
        BOXES("boxes"),
        PACKS("packs"),
        LBS("lbs"),
        DOZENS("dozens");

        private final String label;
        UnitOfMeasure(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

}
