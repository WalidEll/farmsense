package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType type;

    private String referenceNumber;

    private String batchNumber;

    private String serialNumber;

    @Column(nullable = false)
    private BigDecimal quantity;

    private BigDecimal unitPrice;

    @Column(name = "total_value", nullable = true, precision = 12, scale = 2)
    private BigDecimal totalValue;

    private Double weight;

    private String location;

    @Enumerated(EnumType.STRING)
    private QualityGrade qualityGrade;

    private String condition;

    private LocalDate movementDate;

    private String description;

    private String source;

    private String destination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flock_id")
    private Flock flock;

    @Enumerated(EnumType.STRING)
    private MovementReason reason;

    private String notes;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public enum MovementType {
        IN, OUT, ADJUSTMENT, TRANSFER, CONSUMPTION, WASTAGE, RETURN, SALE, PURCHASE
    }

    public enum MovementReason {
        PURCHASE, SALES, FEEDING, VACCINATION, TREATMENT, DISPOSAL, STOLEN, DIED,
        PRODUCTION, HARVEST, TRANSFER_IN, TRANSFER_OUT, STOCKtaking_ADJUSTMENT, DAMAGE, RETURN, OTHER
    }

    public enum QualityGrade {
        PRIME, GOOD, FAIR, POOR, DAMAGED
    }
}
