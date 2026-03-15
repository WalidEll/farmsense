package ma.farmsense.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Access(AccessType.FIELD)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Column(nullable = false)
    private String category;

    private String subcategory;

    @Column(nullable = false)
    private BigDecimal amount;

    private Double quantity;

    private BigDecimal unitPrice;

    @Column(nullable = false)
    private LocalDate transactionDate;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String referenceNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private Instant approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flock_id")
    private Flock flock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_plan_id")
    private CropPlan cropPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_location_id")
    private FarmLocation farmLocation;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "transaction_tags",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Transaction() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public Team getTeam() { return this.team; }
    public void setTeam(Team team) { this.team = team; }

    public TransactionType getType() { return this.type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getCategory() { return this.category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubcategory() { return this.subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }

    public BigDecimal getAmount() { return this.amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Double getQuantity() { return this.quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return this.unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public LocalDate getTransactionDate() { return this.transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public String getDescription() { return this.description; }
    public void setDescription(String description) { this.description = description; }

    public Supplier getSupplier() { return this.supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public Customer getCustomer() { return this.customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public PaymentMethod getPaymentMethod() { return this.paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReferenceNumber() { return this.referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public Receipt getReceipt() { return this.receipt; }
    public void setReceipt(Receipt receipt) { this.receipt = receipt; }

    public ApprovalStatus getApprovalStatus() { return this.approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public User getApprovedBy() { return this.approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }

    public Instant getApprovedAt() { return this.approvedAt; }
    public void setApprovedAt(Instant approvedAt) { this.approvedAt = approvedAt; }

    public Flock getFlock() { return this.flock; }
    public void setFlock(Flock flock) { this.flock = flock; }

    public CropPlan getCropPlan() { return this.cropPlan; }
    public void setCropPlan(CropPlan cropPlan) { this.cropPlan = cropPlan; }

    public FarmLocation getFarmLocation() { return this.farmLocation; }
    public void setFarmLocation(FarmLocation farmLocation) { this.farmLocation = farmLocation; }

    public String getNotes() { return this.notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Set<Tag> getTags() { return this.tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
