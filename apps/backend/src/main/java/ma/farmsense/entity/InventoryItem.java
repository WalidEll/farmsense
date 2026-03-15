package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "inventory_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID user_id;

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
    @Builder.Default
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

    @Builder.Default
    @Column(name = "is_tracked", nullable = false)
    private Boolean isTracked = false; // Track individual serial numbers

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamptz")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

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
        PCS("pcs"),      // Pieces/units - individual countable items
        KG("kg"),        // Kilograms - weight-based items
        LITERS("liters"), // Liters - liquid items
        BOXES("boxes"),  // Boxes - packaged items
        PACKS("packs"),  // Packs - multi-pack items
        LBS("lbs"),      // Pounds
        DOZENS("dozens") // Dozens (for eggs, chicks)

    }

}
