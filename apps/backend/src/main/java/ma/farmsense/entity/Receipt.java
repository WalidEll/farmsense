package ma.farmsense.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "receipts")
@Access(AccessType.FIELD)
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String filePath;

    private String contentType;

    private Long fileSizeBytes;

    private String ocrVendor;

    private LocalDate ocrDate;

    private BigDecimal ocrAmount;

    private String ocrCategory;

    @Column(columnDefinition = "jsonb")
    private String ocrLineItems;

    @Column(columnDefinition = "TEXT")
    private String ocrRawJson;

    private Double ocrConfidence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OcrStatus ocrStatus = OcrStatus.PENDING;

    @OneToOne(mappedBy = "receipt", fetch = FetchType.LAZY)
    private Transaction transaction;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Receipt() {}

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getId() { return this.id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public Team getTeam() { return this.team; }
    public void setTeam(Team team) { this.team = team; }

    public String getOriginalFilename() { return this.originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getFilePath() { return this.filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getContentType() { return this.contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getFileSizeBytes() { return this.fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public String getOcrVendor() { return this.ocrVendor; }
    public void setOcrVendor(String ocrVendor) { this.ocrVendor = ocrVendor; }

    public LocalDate getOcrDate() { return this.ocrDate; }
    public void setOcrDate(LocalDate ocrDate) { this.ocrDate = ocrDate; }

    public BigDecimal getOcrAmount() { return this.ocrAmount; }
    public void setOcrAmount(BigDecimal ocrAmount) { this.ocrAmount = ocrAmount; }

    public String getOcrCategory() { return this.ocrCategory; }
    public void setOcrCategory(String ocrCategory) { this.ocrCategory = ocrCategory; }

    public String getOcrLineItems() { return this.ocrLineItems; }
    public void setOcrLineItems(String ocrLineItems) { this.ocrLineItems = ocrLineItems; }

    public String getOcrRawJson() { return this.ocrRawJson; }
    public void setOcrRawJson(String ocrRawJson) { this.ocrRawJson = ocrRawJson; }

    public Double getOcrConfidence() { return this.ocrConfidence; }
    public void setOcrConfidence(Double ocrConfidence) { this.ocrConfidence = ocrConfidence; }

    public OcrStatus getOcrStatus() { return this.ocrStatus; }
    public void setOcrStatus(OcrStatus ocrStatus) { this.ocrStatus = ocrStatus; }

    public Transaction getTransaction() { return this.transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public Instant getCreatedAt() { return this.createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return this.updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
