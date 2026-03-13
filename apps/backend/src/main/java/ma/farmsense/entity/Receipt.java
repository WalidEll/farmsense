package ma.farmsense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "receipts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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
    @Builder.Default
    private OcrStatus ocrStatus = OcrStatus.PENDING;

    @OneToOne(mappedBy = "receipt", fetch = FetchType.LAZY)
    private Transaction transaction;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }
}
