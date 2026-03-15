package ma.farmsense.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@Access(AccessType.FIELD)
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
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public StockMovement() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public Team getTeam() { return this.team; }
    public void setTeam(Team team) { this.team = team; }

    public InventoryItem getInventoryItem() { return this.inventoryItem; }
    public void setInventoryItem(InventoryItem inventoryItem) { this.inventoryItem = inventoryItem; }

    public MovementType getType() { return this.type; }
    public void setType(MovementType type) { this.type = type; }

    public String getReferenceNumber() { return this.referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getBatchNumber() { return this.batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getSerialNumber() { return this.serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public BigDecimal getQuantity() { return this.quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return this.unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTotalValue() { return this.totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public Double getWeight() { return this.weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getLocation() { return this.location; }
    public void setLocation(String location) { this.location = location; }

    public QualityGrade getQualityGrade() { return this.qualityGrade; }
    public void setQualityGrade(QualityGrade qualityGrade) { this.qualityGrade = qualityGrade; }

    public String getCondition() { return this.condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public LocalDate getMovementDate() { return this.movementDate; }
    public void setMovementDate(LocalDate movementDate) { this.movementDate = movementDate; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public String getSource() { return this.source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return this.destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Supplier getSupplier() { return this.supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public Customer getCustomer() { return this.customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Flock getFlock() { return this.flock; }
    public void setFlock(Flock flock) { this.flock = flock; }

    public MovementReason getReason() { return this.reason; }
    public void setReason(MovementReason reason) { this.reason = reason; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

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
